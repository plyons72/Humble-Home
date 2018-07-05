% Momentum-based; compute velocity and momentum at various points; a previously detected peak will gain momentum as it climbs down and lose momentum as it climbs the next peak; the point where it loses all momentum is the next peak 
%
% References:
% Harmer K., Howells G., Sheng W., Fairhurst M., Deravi F. (2008). “A Peak-Trough Detection Algorithm Based on Momentum.” Proc. IEEE Congress on Image and Signal Processing (CISP), pp. 454 – 458.

function [] = momentum(data)

subplot(4, 1, 3);
title('Momentum');
grid on
hold on
plot(data);

peaks = zeros(length(data), 1);
troughs = zeros(length(data), 1);

% A real number between 0 (frictionless) and 1 (maximum friction; no movement)
friction = 0;
% Set initial minimum momentum
minMomentum = 0.01;
% The algorithm is executing
executing = false;
% Number of peaks and troughs encountered
ptCount = 0;
% Time difference between the previous sample and current sample (in this case, 15 minutes)
interval = 15*60;   % in seconds?
% How many samples have been processed
sampleCount = 0;
% Current momentum
momentum = 0;
% Previous peak or trough
ptSample = 0;
% Previous sample value
prevSample = 0;
% Flag to determine whether the samples need to be negated
flip = false;

for i = 1 : length(data)
    % Current sample value
    sample = data(i);
    
    if (executing == false && abs(momentum) < minMomentum)
        if (sampleCount > 0 && ptCount > 0)
            momentum = momentum + (sample - ptSample) / interval;
        else
            if (sampleCount > 0)
                momentum = momentum + (sample - prevSample) / interval;
                if (sample < prevSample)
                   flip = false; 
                else
                   flip = true;
                end
            end            
        end
        if (abs(momentum) >= minMomentum && sampleCount > 0)
            executing = true;
            momentum = abs(momentum) * (1 - friction);

        end
    else
        if executing == false
            executing = true;
        end
    end
    if executing == true
        if flip == true
            sample = sample * -1;
            prevSample = prevSample * -1;
        end
        if sample < ptSample
            ptSample = sample;
        end
        velocity = -1 * (sample - prevSample) / interval;
        if velocity < 0
            friction = friction * -1;
        end
        momentum = momentum + (1 - friction) * velocity;
        if momentum <= 0
            if flip == true
                flip = false
                % PTSample is a peak
                peaks(sampleCount + 1) = ptSample;
                plot(sampleCount + 1, ptSample, 'og');
                % Set PTSample to whatever is smallest between PTSample and Sample
                ptSample = min(ptSample, sample);
            else
                flip = true
                % PTSample is a trough
                troughs(sampleCount + 1) = ptSample;
                plot(sampleCount + 1, ptSample, 'or');
                % Set PTSample to whatever is the smallest between negated PTSample or negated Sample
                ptSample = min(-1 * ptSample, -1 * sample);
            end
            momentum = abs(sample - ptSample) * (1 - friction);
            ptCount = ptCount + 1;
            executing = false;
        end
    end    
    prevSample = sample;
    sampleCount = sampleCount + 1;
end

end

