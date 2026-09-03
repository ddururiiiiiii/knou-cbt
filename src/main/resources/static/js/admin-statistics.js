(function () {
    const ACCENT = '#4f46e5';
    const ACCENT_SOFT = 'rgba(79, 70, 229, 0.12)';
    const INK = '#1e2230';
    const MUTED = '#6b7280';

    const baseFont = { family: "'Pretendard', -apple-system, sans-serif" };

    function parseLabelsJson(value) {
        if (!value) return [];
        try {
            return JSON.parse(value);
        } catch (e) {
            return [];
        }
    }

    function parseCsvNumbers(value) {
        if (!value) return [];
        return value.split(',').map(Number);
    }

    function initTrendChart() {
        const wrap = document.getElementById('trendChartWrap');
        const canvas = document.getElementById('trendChart');
        if (!wrap || !canvas) return;

        const labels = parseLabelsJson(wrap.dataset.labels);
        const counts = parseCsvNumbers(wrap.dataset.counts);

        new Chart(canvas, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: '응시 수',
                    data: counts,
                    borderColor: ACCENT,
                    backgroundColor: ACCENT_SOFT,
                    fill: true,
                    tension: 0.35,
                    pointRadius: 3,
                    pointBackgroundColor: ACCENT,
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        titleFont: baseFont,
                        bodyFont: baseFont
                    }
                },
                scales: {
                    x: {
                        ticks: { color: MUTED, font: baseFont },
                        grid: { display: false }
                    },
                    y: {
                        beginAtZero: true,
                        ticks: { color: MUTED, font: baseFont, precision: 0 },
                        grid: { color: '#f0f1f5' }
                    }
                }
            }
        });
    }

    function initSubjectChart() {
        const wrap = document.getElementById('subjectChartWrap');
        const canvas = document.getElementById('subjectChart');
        if (!wrap || !canvas) return;

        const labels = parseLabelsJson(wrap.dataset.labels);
        const counts = parseCsvNumbers(wrap.dataset.counts);

        new Chart(canvas, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: '응시 수',
                    data: counts,
                    backgroundColor: ACCENT,
                    borderRadius: 6,
                    maxBarThickness: 22
                }]
            },
            options: {
                indexAxis: 'y',
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false }
                },
                scales: {
                    x: {
                        beginAtZero: true,
                        ticks: { color: MUTED, font: baseFont, precision: 0 },
                        grid: { color: '#f0f1f5' }
                    },
                    y: {
                        ticks: { color: INK, font: baseFont },
                        grid: { display: false }
                    }
                }
            }
        });
    }

    function initSignupTrendChart() {
        const wrap = document.getElementById('signupTrendChartWrap');
        const canvas = document.getElementById('signupTrendChart');
        if (!wrap || !canvas) return;

        const labels = parseLabelsJson(wrap.dataset.labels);
        const counts = parseCsvNumbers(wrap.dataset.counts);

        new Chart(canvas, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: '가입자 수',
                    data: counts,
                    borderColor: ACCENT,
                    backgroundColor: ACCENT_SOFT,
                    fill: true,
                    tension: 0.35,
                    pointRadius: 3,
                    pointBackgroundColor: ACCENT,
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        titleFont: baseFont,
                        bodyFont: baseFont
                    }
                },
                scales: {
                    x: {
                        ticks: { color: MUTED, font: baseFont },
                        grid: { display: false }
                    },
                    y: {
                        beginAtZero: true,
                        ticks: { color: MUTED, font: baseFont, precision: 0 },
                        grid: { color: '#f0f1f5' }
                    }
                }
            }
        });
    }

    document.addEventListener('DOMContentLoaded', function () {
        if (typeof Chart === 'undefined') return;
        initTrendChart();
        initSubjectChart();
        initSignupTrendChart();
    });
})();
