% Moving average based; the time-series is smoothed using a moving average filter; values larger than x times the standard deviation of the entire (smoothed) time-series are considered peaks 
%
% References:
% Vlachos M., Meek C., Vagena Z., Gunopulos D. (2004). “Identification of Similarities, Periodicities and Bursts for Online Search Queries”, Proc. SIGMOD 2004 Conf., ACM Press, pp. 131 – 142.
% https://en.wikipedia.org/wiki/Moving_average#Cumulative_moving_average
% https://stackoverflow.com/questions/22583391/peak-recognition-in-realtime-timeseries-data/22640362#22640362

function [] = moving_avg(data)

% the lag of the moving window
lag = 4;    
% the z-score at which the algorithm signals
threshold = 3.5;
% the influence (between 0 and 1) of new signals on the mean and standard deviation
influence = 0.35;

% Initialize signal results to zeros
signals = zeros(length(data), 1);
% Initialize filtered series
filteredData = data(1: lag + 1);
% Initialize average filter
avgFilter(lag + 1, 1) = mean(data(1 : lag + 1));
% Initialize standard deviation filter
stdFilter(lag + 1, 1) = std(data(1 : lag + 1));

for i = lag + 2 : length(data)
    if abs(data(i) - avgFilter(i - 1)) > threshold * stdFilter(i - 1)
        if data(i) > avgFilter(i - 1)
            % Positive signal (peak)
            signals(i) = 1;
        else
            % Negative signal (trough)
            signals(i) = -1;
        end
        filteredData(i) = influence * data(i) + (1 - influence) * filteredData(i - 1);
    else
        % No signal
        signals(i) = 0;
        filteredData(i) = data(i);
    end
    % Adjust the filters
    avgFilter(i) = mean(filteredData(i - lag : i));
    stdFilter(i) = std(filteredData(i - lag : i));
end

subplot(4, 1, 4);
title('Moving Average');
grid on
hold on
plot(avgFilter, 'r');
plot(signals, 'b');

end

