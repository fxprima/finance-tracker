document.addEventListener('DOMContentLoaded', function () {
    const exportBtn = document.getElementById('exportPdfBtn');
    const form = document.getElementById('exportReportForm');

    if (!exportBtn || !form) return;

    exportBtn.addEventListener('click', async function () {
        exportBtn.disabled = true;
        exportBtn.textContent = 'Generating...';

        try {
            // 1. HEATMAP (pakai html2canvas)
            const heatmapEl = document.getElementById('expenses-heatmap');
            if (heatmapEl) {
                const canvasHeatmap = await html2canvas(heatmapEl, {
                    scale: 2,
                    useCORS: true
                });
                const heatmapDataUrl = canvasHeatmap.toDataURL('image/png');
                document.getElementById('heatmapImageBase64').value = heatmapDataUrl;
            }

            // 2. CATEGORY CHART (Pie / Bar) – ambil yang lagi kelihatan, misalnya pie
            let categoryChart = null;
            if (typeof Chart !== 'undefined' && Chart.getChart) {
                categoryChart = Chart.getChart('categoryPieChart'); // id canvas
            }
            if (categoryChart) {
                const chartDataUrl = categoryChart.toBase64Image();
                document.getElementById('categoryChartImageBase64').value = chartDataUrl;
            }

            // 3. TREND CHART
            let trendChart = null;
            if (typeof Chart !== 'undefined' && Chart.getChart) {
                trendChart = Chart.getChart('monthlyTrendChart');
            }
            if (trendChart) {
                const trendDataUrl = trendChart.toBase64Image();
                document.getElementById('trendChartImageBase64').value = trendDataUrl;
            }

            // 4. Submit form ke backend
            form.submit();
        } catch (err) {
            console.error('[export-report] failed', err);
            alert('Gagal generate report image. Coba lagi.');
            exportBtn.disabled = false;
            exportBtn.textContent = 'Export as PDF';
        }
    });
});

