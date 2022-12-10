data =readtable('Log24.csv');
maxTime=24;

[a1Pickup,a1process,a1pickupprocess,a1tohos,a1all]=meanTime(data,0);
[bPickup,bprocess,bpickupprocess,btohos,ball]=meanTime(data,1);
[a2Pickup,a2process,a2pickupprocess,a2tohos,a2all]=meanTime(data,2);

cia1=[confidenceInterval(a1Pickup);confidenceInterval(a1process);confidenceInterval(a1pickupprocess);confidenceInterval(a1tohos);confidenceInterval(a1all)];
cib=[confidenceInterval(bPickup);confidenceInterval(bprocess);confidenceInterval(bpickupprocess);confidenceInterval(btohos);confidenceInterval(ball)];
cia2=[confidenceInterval(a2Pickup);confidenceInterval(a2process);confidenceInterval(a2pickupprocess);confidenceInterval(a2tohos);confidenceInterval(a2all)];

array2table(cia1,'VariableNames',{'N','Mean','Lower CI','Upper CI'},'RowNames',{'Pick Up','Process','Pickup and Process','To Hospital','Total'})
array2table(cib,'VariableNames',{'N','Mean','Lower CI','Upper CI'},'RowNames',{'Pick Up','Process','Pickup and Process','To Hospital','Total'})
array2table(cia2,'VariableNames',{'N','Mean','Lower CI','Upper CI'},'RowNames',{'Pick Up','Process','Pickup and Process','To Hospital','Total'})


creation=data(strcmp(data{:,1}, 'Creation') & (data{:,5}==0), 2);
arrive=data(strcmp(data{:,1}, 'Ambulance at Patient') & (data{:,5}==0), 2);
process=data(strcmp(data{:,1}, 'Patient Processed') & (data{:,5}==0), 2);

timeBeforePickup=arrive{:,1}-creation{:,1};
a1WaitedMoreThanFifteen=timeBeforePickup(timeBeforePickup>0.25);

array2table([confidenceInterval(a1WaitedMoreThanFifteen) length(a1WaitedMoreThanFifteen)/size(creation,1)],'VariableNames',{'N','Mean','Lower CI','Upper CI','% of total A1'})

timebeforProcess=process{:,1}-creation{:,1};
a1wbp=timebeforProcess(timebeforProcess>.25);
array2table([confidenceInterval(a1wbp) length(a1wbp)/size(creation,1)],'VariableNames',{'N','Mean','Lower CI','Upper CI','% of total A1'})


shiftchanges=data(strcmp(data{:,1},'Crew Change'),[2,4]);
makeFig(shiftchanges,maxTime)

function [toPickup,toProcess,pickupProcess,toHospital,full]=meanTime(d,t)
    creation=d(strcmp(d{:,1}, 'Creation') & (d{:,5}==t), 2);
    AatP=d(strcmp(d{:,1}, 'Ambulance at Patient') & (d{:,5}==t), 2);
    process=d(strcmp(d{:,1}, 'Patient Processed') & (d{:,5}==t), 2);
    atHos=d(strcmp(d{:,1}, 'Production complete') & (d{:,5}==t), 2);
   
    toPickup=AatP{:,1}-creation{:,1};
    toProcess=process{:,1}-AatP{:,1};
    pickupProcess=toProcess+toPickup;
    toHospital=atHos{:,1}-process{:,1};
    full=atHos{:,1}-creation{:,1};
    
end

function []=makeFig(sc,maxTime)
    labels=(sc{:,2});
    
    for i=1:length(labels)
        v=strsplit(labels{i} ," ");
        sc(i,2)=v(2);
        sc(i,1)=table(round(sc{i,1}));
    end

    sched=zeros(35,maxTime);
    for i=1:size(sc,1)
        sched(cellfun(@str2num,sc{i,2}),sc{i,1})=100;
    end

    figure
    image(sched,'CDataMapping','scaled')
    ylabel('Ambulances')
    xlabel("Time in Hours")
    
    for i=1:35
        yline(i,"Color","black")
    end
%{
    for i=1:maxTime
            xline(i,"Color","black")
    end
%}
    
    axis([1 maxTime 1 35])


end


function [ci]= confidenceInterval(d) 
    n=length(d);
    mu=sum(d)/n;
    s= (sum((d-mu).^2)/(n-1))^(.5);
    upper=mu+(1.96)*(s/(n)^(.5));
    lower=mu-(1.96)*(s/(n)^(.5));
    ci=[n mu lower upper];
end
