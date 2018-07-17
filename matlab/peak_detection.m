function [] = peak_detection(data_file)

clf

data = load(data_file);

ts = timeseries(data(:,1));
ts = setuniformtime(ts, 'Interval', 15);

ts.Name = 'Demand';
ts.DataInfo.Units = 'kW';
ts.TimeInfo.Units = 'minutes';
ts.TimeInfo.Format = 'hh:mm';

ts.Time = ts.Time - ts.Time(1);

subplot(4, 1, 1);
grid on
hold on
plot(ts, ':b');
title(data_file);

cwt_detect(data);
momentum(data);
moving_avg(data);