// js/insight/category-pie-chart.js

document.addEventListener('DOMContentLoaded', function () {
    // ambil data dari global
    const txList = window.transactions || window.TRANSACTIONS || window.FINANCE_TRANSACTIONS;

    if (!txList || txList.length === 0) {
        console.log('[category-pie-chart] no transactions, skip');
        return;
    }

    const canvas = document.getElementById('categoryPieChart');
    if (!canvas) {
        console.log('[category-pie-chart] canvas not found');
        return;
    }

    // aggregate jumlah EXPENSE per kategori
    const categoryTotals = {};

    txList.forEach(tx => {
        // cuma hitung expense
        if (tx.type !== 'EXPENSE') return;

        const cat = tx.category || 'Uncategorized';
        const amount = tx.amount || 0;

        categoryTotals[cat] = (categoryTotals[cat] || 0) + amount;
    });

    const labels = Object.keys(categoryTotals);
    const data = Object.values(categoryTotals);

    if (labels.length === 0) {
        console.log('[category-pie-chart] no expense data');
        return;
    }

    const total = data.reduce((a, b) => a + b, 0);

    // kalau mau dipakai file lain (opsional)
    window.CATEGORY_TOTALS = {
        raw: categoryTotals,
        labels,
        data,
        total
    };

    // bikin pie chart
    new Chart(canvas, {
        type: 'pie',
        data: {
            labels: labels,
            datasets: [{
                data: data,
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,   // 🔥 penting: biar ngikutin h-72 / h-80
            layout: {
                padding: 0
            },
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        boxWidth: 12,
                        boxHeight: 12,
                        padding: 10
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function (context) {
                            const label = context.label || '';
                            const value = context.raw || 0;
                            const percentage = total > 0
                                ? (value / total * 100)
                                : 0;

                            const formattedValue = value.toLocaleString('id-ID', {
                                style: 'currency',
                                currency: 'IDR',
                                maximumFractionDigits: 0
                            });

                            return `${label}: ${formattedValue} (${percentage.toFixed(1)}%)`;
                        }
                    }
                }
            }
        }

    });
});
    