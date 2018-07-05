% Continuous Wavelet Transform (CWT) based on pattern-matching
% 
% References:
% Du P., Kibbe W.A., Lin S.M. (2006). “Improved Peak Detection in Mass Spectrum by Incorporating Continuous Wavelet Transform-based Pattern Matching.” Bioinformatics, vol. 22, no. 17, pp. 2059 – 2065. 
% Wee A., Grayden D., Zhu Y., Petkovic-Duran K., Smith D. (2008). “A continuous wavelet transform algorithm for peak detection.” Electrphoresis, no. 29, pp. 4215 – 4225.
% MathWorks. “cwtft2: 2-D continuous wavelet transform.” Accessed on: Jul, 1, 2018. [Online]. Available: https://www.mathworks.com/help/wavelet/ref/cwtft2.html?s_tid=doc_ta

function [] = cwt_detect(data)

subplot(4, 1, 2);
title('CWT')
grid on
hold on

coeffs = cwt(data, 1 : length(data), 'sym4');
plot(coeffs(30,:));

local_max = max(CWTcoeffs, [], size(CWTcoeff, 1));
%plot(local_max);

end

