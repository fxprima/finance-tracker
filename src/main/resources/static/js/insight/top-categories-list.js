// js/insight/top-categories-list.js

document.addEventListener('DOMContentLoaded', function () {
    const listEl = document.getElementById('top-categories-list');
    if (!listEl) {
        console.log('[top-categories-list] container not found');
        return;
    }

    // pakai hasil CATEGORY_TOTALS kalau sudah ada
    let categoryTotals = null;
    let total = 0;

    if (window.CATEGORY_TOTALS) {
        categoryTotals = window.CATEGORY_TOTALS.raw;
        total = window.CATEGORY_TOTALS.total;
    } else {
        // fallback: hitung ulang dari transaksi
        const txList = window.transactions || window.TRANSACTIONS || window.FINANCE_TRANSACTIONS;

        if (!txList || txList.length === 0) {
            console.log('[top-categories-list] no transactions');
            return;
        }

        categoryTotals = {};
        txList.forEach(tx => {
            if (tx.type !== 'EXPENSE') return;

            const cat = tx.category || 'Uncategorized';
            const amount = tx.amount || 0;

            categoryTotals[cat] = (categoryTotals[cat] || 0) + amount;
        });

        total = Object.values(categoryTotals).reduce((a, b) => a + b, 0);
    }

    const entries = Object.entries(categoryTotals || {})
        .map(([name, amount]) => ({
            name,
            amount,
            percentage: total > 0 ? (amount / total) * 100 : 0
        }))
        .sort((a, b) => b.amount - a.amount);

    if (entries.length === 0) {
        console.log('[top-categories-list] no expense data');
        return;
    }

    // limit berapa banyak mau ditampilkan (misal top 6)
    const TOP_N = 6;
    const topEntries = entries.slice(0, TOP_N);

    // bikin HTML list item per kategori
    const html = topEntries.map((item, index) => {
        const rank = index + 1;

        // untuk lebar bar, pakai persentase dari total (atau bisa juga dari max amount)
        const barWidth = Math.max(5, Math.min(100, item.percentage)); // jaga-jaga min 5%

        const formattedAmount = item.amount.toLocaleString('id-ID', {
            style: 'currency',
            currency: 'IDR',
            maximumFractionDigits: 0
        });

        return `
            <li class="flex flex-col gap-1">
                <div class="flex items-center justify-between">
                    <div class="flex items-center gap-2">
                        <span class="text-xs font-semibold text-gray-400 dark:text-slate-500">
                            ${rank}
                        </span>
                        <span class="text-sm font-medium text-gray-800 dark:text-slate-50">
                            ${item.name}
                        </span>
                    </div>
                    <div class="text-right">
                        <p class="text-sm font-semibold text-gray-900 dark:text-slate-50">
                            ${formattedAmount}
                        </p>
                        <p class="text-xs text-gray-500 dark:text-slate-400">
                            ${item.percentage.toFixed(1)}%
                        </p>
                    </div>
                </div>
                <div class="w-full h-1.5 rounded-full bg-gray-100 dark:bg-slate-800 overflow-hidden">
                    <div class="h-full rounded-full bg-blue-500" style="width: ${barWidth}%;"></div>
                </div>
            </li>
        `;
    }).join('');

    listEl.innerHTML = html;
});
