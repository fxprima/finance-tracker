// js/insight/category-bart-chart.js

document.addEventListener('DOMContentLoaded', function () {
    // ambil data dari global (sama kayak pie chart)
    const txList = window.transactions || window.TRANSACTIONS || window.FINANCE_TRANSACTIONS;

    if (!txList || txList.length === 0) {
        console.log('[category-bar-chart] no transactions, skip');
        return;
    }

    const canvas = document.getElementById('categoryBarChart');
    if (!canvas) {
        console.log('[category-bar-chart] canvas not found');
        return;
    }

    // hitung total per kategori (hanya EXPENSE)
    const categoryTotals = {};
    txList.forEach(tx => {
        const type = (tx.type || tx.transactionType || '').toString().toUpperCase();
        if (type !== 'EXPENSE') return;

        const cat = tx.category || 'Uncategorized';
        const amount = Number(tx.amount) || 0;

        categoryTotals[cat] = (categoryTotals[cat] || 0) + amount;
    });

    const labels = Object.keys(categoryTotals);
    const data = Object.values(categoryTotals);

    if (labels.length === 0) {
        console.log('[category-bar-chart] no expense data');
        return;
    }

    const ctx = canvas.getContext('2d');

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                data: data,
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false, // biar ngikut h-72 / h-80 di HTML
            scales: {
                y: {
                    beginAtZero: true
                }
            },
            plugins: {
                legend: { display: false }
            }
        }
    });
});
