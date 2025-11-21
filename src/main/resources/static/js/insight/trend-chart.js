document.addEventListener('DOMContentLoaded', function () {
    const canvas = document.getElementById('monthlyTrendChart');
    if (!canvas) {
        console.log('[trend] canvas not found');
        return;
    }

    const txList =
        window.transactions ||
        window.FINANCE_TRANSACTIONS ||
        window.TRANSACTIONS ||
        [];

    if (!txList || txList.length === 0) {
        console.log('[trend] no transactions');
        return;
    }

    const msPerDay = 24 * 60 * 60 * 1000;

    // --- cari min & max date dari transaksi valid ---
    let minDate = null;
    let maxDate = null;

    txList.forEach(tx => {
        if (!tx.date) return;
        const d = new Date(tx.date);
        if (isNaN(d.getTime())) return;

        if (!minDate || d < minDate) minDate = d;
        if (!maxDate || d > maxDate) maxDate = d;
    });

    if (!minDate || !maxDate) {
        console.log('[trend] no valid dates');
        return;
    }

    // normalize ke 00:00
    minDate = new Date(minDate.getFullYear(), minDate.getMonth(), minDate.getDate());
    maxDate = new Date(maxDate.getFullYear(), maxDate.getMonth(), maxDate.getDate());

    const spanDays = Math.floor((maxDate - minDate) / msPerDay) + 1;

    // --- tentukan mode: daily / weekly / monthly ---
    let mode;
    if (spanDays <= 31) {
        mode = 'daily';           // per hari, stacked expenses by category
    } else if (spanDays <= 90) {
        mode = 'weeklyExpense';   // per minggu, stacked expenses by category
    } else {
        mode = 'monthly';         // per bulan, income/expense/cashflow
    }

    console.log('[trend] mode =', mode, 'spanDays =', spanDays);

    // --- grouping data dasar (dipakai untuk label & monthly) ---
    // key -> { income, expense, cashflow, order, label }
    const groups = {};

    txList.forEach(tx => {
        if (!tx.date) return;

        const d = new Date(tx.date);
        if (isNaN(d.getTime())) return;

        // cuma yang di dalam range
        if (d < minDate || d > maxDate) return;

        const typeRaw = (tx.type || tx.transactionType || '').toString().toUpperCase();
        const amount = Number(tx.amount) || 0;

        let key;
        let order;
        let label;

        if (mode === 'daily') {
            const y = d.getFullYear();
            const m = d.getMonth(); // 0–11
            const day = d.getDate();
            key = `${y}-${String(m + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
            order = d.getTime();
            label = d.toLocaleDateString('id-ID', {
                day: '2-digit',
                month: 'short'
            });
        } else if (mode === 'weeklyExpense') {
            const diffDays = Math.floor((d - minDate) / msPerDay);
            const weekIndex = Math.floor(diffDays / 7); // 0,1,2,...
            key = `W${weekIndex}`;
            order = weekIndex;
            label = `Minggu ${weekIndex + 1}`;
        } else {
            const y = d.getFullYear();
            const m = d.getMonth();
            key = `${y}-${String(m + 1).padStart(2, '0')}`;
            order = y * 12 + m;

            const tmp = new Date(y, m, 1);
            label = tmp.toLocaleDateString('id-ID', {
                month: 'short',
                year: '2-digit'
            });
        }

        if (!groups[key]) {
            groups[key] = {
                income: 0,
                expense: 0,
                cashflow: 0,
                order,
                label
            };
        }

        if (typeRaw === 'INCOME') {
            groups[key].income += amount;
        } else if (typeRaw === 'EXPENSE') {
            groups[key].expense += amount;
        }
    });

    // hitung cashflow tiap group (income - expense)
    Object.values(groups).forEach(g => {
        g.cashflow = g.income - g.expense;
    });

    const keys = Object.keys(groups).sort((a, b) => groups[a].order - groups[b].order);
    if (keys.length === 0) {
        console.log('[trend] no grouped data');
        return;
    }

    const labels = keys.map(k => groups[k].label);
    const incomeData = keys.map(k => groups[k].income);
    const expenseData = keys.map(k => groups[k].expense);
    const cashFlowData = keys.map(k => groups[k].cashflow);

    const ctx = canvas.getContext('2d');

    // --- tentukan chart type dan datasets berdasarkan mode ---
    let chartType;
    let datasets;
    let titleText;

    if (mode === 'daily') {
        // DAILY: stacked expenses by category (per tanggal)
        chartType = 'bar';
        titleText = 'Daily Expenses by Category';

        const dailyCategoryTotals = {};   // key dateKey -> { cat: amount }
        const categoryTotalsOverall = {}; // cat -> total over all days

        txList.forEach(tx => {
            if (!tx.date) return;

            const d = new Date(tx.date);
            if (isNaN(d.getTime())) return;
            if (d < minDate || d > maxDate) return;

            const typeRaw = (tx.type || tx.transactionType || '').toString().toUpperCase();
            if (typeRaw !== 'EXPENSE') return;

            const y = d.getFullYear();
            const m = d.getMonth();
            const day = d.getDate();
            const dateKey = `${y}-${String(m + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;

            const cat =
                tx.category ||
                tx.subCategory ||
                'Others';

            const amount = Number(tx.amount) || 0;

            if (!dailyCategoryTotals[dateKey]) {
                dailyCategoryTotals[dateKey] = {};
            }
            dailyCategoryTotals[dateKey][cat] =
                (dailyCategoryTotals[dateKey][cat] || 0) + amount;

            categoryTotalsOverall[cat] =
                (categoryTotalsOverall[cat] || 0) + amount;
        });

        const sortedCategories = Object.keys(categoryTotalsOverall)
            .sort((a, b) => categoryTotalsOverall[b] - categoryTotalsOverall[a]);

        const maxCategories = 4;
        const topCategories = sortedCategories.slice(0, maxCategories);
        const useOthers = sortedCategories.length > maxCategories;

        const categoriesForChart = useOthers
            ? [...topCategories, 'Others']
            : topCategories;

        datasets = [];

        categoriesForChart.forEach(catName => {
            const data = keys.map(dateKey => {
                const catMap = dailyCategoryTotals[dateKey] || {};

                if (catName === 'Others' && useOthers) {
                    let sum = 0;
                    Object.entries(catMap).forEach(([c, amount]) => {
                        if (!topCategories.includes(c)) {
                            sum += amount;
                        }
                    });
                    return sum;
                }

                return catMap[catName] || 0;
            });

            datasets.push({
                label: catName,
                data,
                borderWidth: 1,
                stack: 'daily'
            });
        });

    } else if (mode === 'weeklyExpense') {
        // WEEKLY: stacked expenses by category (top N + Others)
        chartType = 'bar';
        titleText = 'Weekly Expenses by Category';

        const weeklyCategoryTotals = {};  // key "W0","W1"... -> { cat: amount }
        const categoryTotalsOverall = {}; // cat -> total over all weeks

        txList.forEach(tx => {
            if (!tx.date) return;

            const d = new Date(tx.date);
            if (isNaN(d.getTime())) return;

            if (d < minDate || d > maxDate) return;

            const typeRaw = (tx.type || tx.transactionType || '').toString().toUpperCase();
            if (typeRaw !== 'EXPENSE') return;

            const diffDays = Math.floor((d - minDate) / msPerDay);
            const weekIndex = Math.floor(diffDays / 7);
            const weekKey = `W${weekIndex}`;

            const cat =
                tx.category ||
                tx.subCategory ||
                'Others';

            const amount = Number(tx.amount) || 0;

            if (!weeklyCategoryTotals[weekKey]) {
                weeklyCategoryTotals[weekKey] = {};
            }
            weeklyCategoryTotals[weekKey][cat] =
                (weeklyCategoryTotals[weekKey][cat] || 0) + amount;

            categoryTotalsOverall[cat] =
                (categoryTotalsOverall[cat] || 0) + amount;
        });

        const sortedCategories = Object.keys(categoryTotalsOverall)
            .sort((a, b) => categoryTotalsOverall[b] - categoryTotalsOverall[a]);

        const maxCategories = 4;
        const topCategories = sortedCategories.slice(0, maxCategories);
        const useOthers = sortedCategories.length > maxCategories;

        const categoriesForChart = useOthers
            ? [...topCategories, 'Others']
            : topCategories;

        datasets = [];

        categoriesForChart.forEach(catName => {
            const data = keys.map(weekKey => {
                const catMap = weeklyCategoryTotals[weekKey] || {};

                if (catName === 'Others' && useOthers) {
                    let sum = 0;
                    Object.entries(catMap).forEach(([c, amount]) => {
                        if (!topCategories.includes(c)) {
                            sum += amount;
                        }
                    });
                    return sum;
                }

                return catMap[catName] || 0;
            });

            datasets.push({
                label: catName,
                data,
                borderWidth: 1,
                stack: 'weekly'
            });
        });

    } else {
        // MONTHLY: Income, Expenses, Cash Flow (sudah termasuk cashflow)
        chartType = 'line';
        datasets = [
            {
                label: 'Income',
                data: incomeData,
                borderWidth: 2,
                tension: 0.3,
                pointRadius: 2
            },
            {
                label: 'Expenses',
                data: expenseData,
                borderWidth: 2,
                tension: 0.3,
                pointRadius: 2
            },
            {
                label: 'Cash Flow',
                data: cashFlowData,
                borderWidth: 2,
                tension: 0.3,
                borderDash: [4, 4],
                pointRadius: 2
            }
        ];
        titleText = 'Monthly Income / Expense / Cashflow';
    }

    new Chart(ctx, {
        type: chartType,
        data: {
            labels,
            datasets
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: {
                mode: 'index',
                intersect: false
            },
            plugins: {
                legend: {
                    display: true,
                    position: 'bottom',
                    labels: {
                        usePointStyle: true,
                        boxWidth: 6
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function (context) {
                            const dsLabel = context.dataset.label || 'Value';
                            const value = context.parsed.y || 0;
                            const formatted = value.toLocaleString('id-ID', {
                                style: 'currency',
                                currency: 'IDR',
                                maximumFractionDigits: 0
                            });
                            return `${dsLabel}: ${formatted}`;
                        }
                    }
                },
                title: {
                    display: true,
                    text: titleText
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    stacked: mode === 'weeklyExpense' || mode === 'daily'
                },
                x: {
                    stacked: mode === 'weeklyExpense' || mode === 'daily'
                }
            }
        }
    });

    // --- subtitle: range tanggal full filter ---
    const subtitleEl = document.getElementById('monthlyTrendSubtitle');
    if (subtitleEl) {
        const startLabel = minDate.toLocaleDateString('id-ID', {
            day: '2-digit',
            month: 'short',
            year: '2-digit'
        });
        const endLabel = maxDate.toLocaleDateString('id-ID', {
            day: '2-digit',
            month: 'short',
            year: '2-digit'
        });
        subtitleEl.textContent = `Range: ${startLabel} – ${endLabel}`;
    }
});
