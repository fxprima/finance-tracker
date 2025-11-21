// js/insight/spending-summary-card.js
// Summary berdasarkan range tanggal, + rata-rata / hari + estimasi jika pola 30 hari

document.addEventListener('DOMContentLoaded', function () {
    const container = document.getElementById('spendingSummaryCard');
    if (!container) {
        console.log('[spending-summary] container not found');
        return;
    }

    const txList = window.transactions || window.FINANCE_TRANSACTIONS || [];
    if (!txList || txList.length === 0) {
        container.innerHTML = `
            <div class="p-4">
                <p class="text-sm text-gray-500 dark:text-slate-400">
                    Tidak ada data transaksi.
                </p>
            </div>
        `;
        return;
    }

    const msPerDay = 24 * 60 * 60 * 1000;

    // min & max date dari transaksi valid
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
        container.innerHTML = `
            <div class="p-4">
                <p class="text-sm text-gray-500 dark:text-slate-400">
                    Tidak ada tanggal transaksi yang valid.
                </p>
            </div>
        `;
        return;
    }

    minDate = new Date(minDate.getFullYear(), minDate.getMonth(), minDate.getDate());
    maxDate = new Date(maxDate.getFullYear(), maxDate.getMonth(), maxDate.getDate());
    const spanDays = Math.floor((maxDate - minDate) / msPerDay) + 1;

    let totalIncome = 0;
    let totalExpense = 0;

    txList.forEach(tx => {
        if (!tx.date) return;
        const d = new Date(tx.date);
        if (isNaN(d.getTime())) return;
        if (d < minDate || d > maxDate) return;

        const typeRaw = (tx.type || tx.transactionType || '').toString().toUpperCase();
        const amount = Number(tx.amount) || 0;

        if (typeRaw === 'INCOME') {
            totalIncome += amount;
        } else if (typeRaw === 'EXPENSE') {
            totalExpense += amount;
        }
    });

    function formatIDR(value) {
        return (Number(value) || 0).toLocaleString('id-ID', {
            style: 'currency',
            currency: 'IDR',
            maximumFractionDigits: 0
        });
    }

    const cashFlow = totalIncome - totalExpense;
    const avgDailyExpense = spanDays > 0 ? totalExpense / spanDays : 0;

    // equivalent pace kalau pattern ini dipakai 30 hari
    const forecast30 = avgDailyExpense * 30;

    const rangeLabelStart = minDate.toLocaleDateString('id-ID', {
        day: '2-digit',
        month: 'short',
        year: '2-digit'
    });
    const rangeLabelEnd = maxDate.toLocaleDateString('id-ID', {
        day: '2-digit',
        month: 'short',
        year: '2-digit'
    });

    const cashFlowColor = cashFlow >= 0 ? 'text-emerald-400' : 'text-rose-400';

    container.innerHTML = `
        <div class="p-4 h-full flex flex-col justify-between">
            <div>
                <p class="text-xs uppercase text-gray-500 mb-2 tracking-wide dark:text-slate-400">
                    Spending Overview
                </p>
                <p class="text-sm text-gray-500 dark:text-slate-400 mb-3">
                    Berdasarkan transaksi dari ${rangeLabelStart} sampai ${rangeLabelEnd}
                    (${spanDays} hari)
                </p>

                <dl class="space-y-2 text-sm">
                    <div class="flex items-center justify-between">
                        <dt class="text-gray-500 dark:text-slate-400">Total Income</dt>
                        <dd class="font-semibold text-emerald-500">
                            ${formatIDR(totalIncome)}
                        </dd>
                    </div>
                    <div class="flex items-center justify-between">
                        <dt class="text-gray-500 dark:text-slate-400">Total Expenses</dt>
                        <dd class="font-semibold text-rose-400">
                            ${formatIDR(totalExpense)}
                        </dd>
                    </div>
                    <div class="flex items-center justify-between">
                        <dt class="text-gray-500 dark:text-slate-400">Cash Flow</dt>
                        <dd class="font-semibold ${cashFlowColor}">
                            ${formatIDR(cashFlow)}
                        </dd>
                    </div>
                </dl>
            </div>

            <div class="mt-4 border-t border-gray-200 pt-3 dark:border-slate-700">
                <p class="text-xs uppercase text-gray-500 mb-2 tracking-wide dark:text-slate-400">
                    Spending Pace
                </p>
                <div class="space-y-1 text-sm">
                    <p class="flex items-center justify-between">
                        <span class="text-gray-500 dark:text-slate-400">
                            Rata-rata pengeluaran / hari
                        </span>
                        <span class="font-semibold text-gray-900 dark:text-slate-50">
                            ${formatIDR(avgDailyExpense)}
                        </span>
                    </p>
                    <p class="flex items-center justify-between">
                        <span class="text-gray-500 dark:text-slate-400">
                            Estimasi jika pola ini 30 hari
                        </span>
                        <span class="font-semibold text-gray-900 dark:text-slate-50">
                            ${formatIDR(forecast30)}
                        </span>
                    </p>
                </div>
                <p class="mt-2 text-[11px] text-gray-500 dark:text-slate-400">
                    Estimasi 30 hari ini cuma konversi dari rata-rata harian pada range yang dipilih.
                </p>
            </div>
        </div>
    `;
});
