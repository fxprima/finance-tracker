
document.addEventListener('DOMContentLoaded', function () {
    const container = document.getElementById('expenses-heatmap');
    if (!container) {
        console.log('[heatmap] container not found');
        return;
    }

    const txList = window.transactions || window.FINANCE_TRANSACTIONS || [];
    if (!txList || txList.length === 0) {
        console.log('[heatmap] no transactions');
        container.innerHTML = `
            <div class="text-xs text-gray-500 italic text-center py-6">
                No data in selected range.
            </div>
        `;
        return;
    }

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
        console.log('[heatmap] invalid dates');
        return;
    }

    const msPerDay = 24 * 60 * 60 * 1000;
    const spanDays = Math.floor((maxDate - minDate) / msPerDay) + 1;

    let mode = 'dailyCalendar';
    if (spanDays <= 31) {
        mode = 'dailyCalendar';
    } else if (spanDays <= 180) {
        mode = 'weekly';
    } else {
        mode = 'monthly';
    }

    console.log('[heatmap] mode =', mode, 'spanDays =', spanDays);

    const baseAlpha = 0.25;
    function makeColor(value, maxValue) {
        if (!value || maxValue === 0) {
            return 'rgba(148, 163, 184, 0.15)';
        }
        const intensity = value / maxValue;
        return `rgba(59, 130, 246, ${baseAlpha + intensity * 0.6})`;
    }

    function formatShortAmount(value) {
        if (!value || value <= 0) return '-';

        if (value >= 1_000_000) {
            const jt = value / 1_000_000;
            return jt.toFixed(jt % 1 === 0 ? 0 : 1).replace(/\.0$/, '') + 'jt';
        }
        if (value >= 1_000) {
            const k = value / 1_000;
            return Math.round(k) + 'k';
        }
        return value.toString();
    }

    function formatRupiah(value) {
        return (Number(value) || 0).toLocaleString('id-ID', {
            style: 'currency',
            currency: 'IDR',
            maximumFractionDigits: 0
        });
    }

    container.innerHTML = '';

    const tooltip = document.createElement('div');
    tooltip.id = 'heatmap-tooltip';
    tooltip.className =
        'hidden fixed z-50 px-4 py-3 rounded-md text-[11px] ' +
        'bg-slate-900/95 text-slate-100 shadow-lg border border-slate-700 ' +
        'min-w-[250px] max-w-[420px] pointer-events-none';

    document.body.appendChild(tooltip);

    function buildMiniBars(data) {
        const total = data.total || 0;
        const categories = data.categories || {};

        const entries = Object.entries(categories)
            .filter(([_, v]) => v > 0)
            .sort((a, b) => b[1] - a[1])
            .slice(0, 5);

        if (!entries.length || total <= 0) {
            return `
                <div class="text-[10px] text-slate-400 mt-1">
                    No expenses in this range.
                </div>
            `;
        }

        return `
            <div class="mt-2 space-y-3">
                ${entries
                    .map(([name, amt]) => {
                        const share = amt / total;
                        const pct = (share * 100).toFixed(1).replace(/\.0$/, '');
                        const width = (share * 100).toFixed(1);

                        return `
                            <div class="flex flex-col gap-1 w-full">
                                <div class="flex justify-between items-center w-full">
                                    <span class="text-[11px] text-slate-200 truncate max-w-[260px]">
                                        ${name}
                                    </span>
                                    <span class="text-[11px] text-slate-300 tabular-nums">
                                        ${pct}%
                                    </span>
                                </div>

                                <div class="w-full h-[6px] rounded-full bg-slate-700 overflow-hidden">
                                    <div class="h-full rounded-full"
                                         style="width:${width}%; background:#60A5FA;"></div>
                                </div>
                            </div>
                        `;
                    })
                    .join('')}
            </div>
        `;
    }



    function showTooltip(ev, data) {
        if (!data) return;

        const title = data.label || '';
        const total = data.total || 0;

        tooltip.innerHTML = `
            <div class="font-semibold mb-0.5">${title}</div>
            <div class="text-[10px] text-slate-300 mb-1">
                Total: <span class="font-medium text-slate-100">${formatRupiah(total)}</span>
            </div>
            ${buildMiniBars(data)}
        `;

        tooltip.classList.remove('hidden');
        positionTooltip(ev);
    }

    function positionTooltip(ev) {
        const offset = 16;
        let x = ev.clientX + offset;
        let y = ev.clientY + offset;

        const rect = tooltip.getBoundingClientRect();
        const vw = window.innerWidth;
        const vh = window.innerHeight;

        if (x + rect.width + 8 > vw) {
            x = ev.clientX - rect.width - offset;
        }
        if (y + rect.height + 8 > vh) {
            y = vh - rect.height - 8;
        }

        tooltip.style.left = x + 'px';
        tooltip.style.top = y + 'px';
    }

    function hideTooltip() {
        tooltip.classList.add('hidden');
    }

    function attachHover(el, data) {
        if (!el) return;
        el.addEventListener('mouseenter', ev => showTooltip(ev, data));
        el.addEventListener('mousemove', positionTooltip);
        el.addEventListener('mouseleave', hideTooltip);
    }

    if (mode === 'dailyCalendar') {
        const year = minDate.getFullYear();
        const month = minDate.getMonth();
        const daysInMonth = new Date(year, month + 1, 0).getDate();

        const dailyTotals = {};
        const dailyCategories = {};

        txList.forEach(tx => {
            if (!tx.date) return;
            const d = new Date(tx.date);
            if (isNaN(d.getTime())) return;

            if (d.getFullYear() !== year || d.getMonth() !== month) return;

            const typeRaw = (tx.type || tx.transactionType || '').toString().toUpperCase();
            if (typeRaw !== 'EXPENSE') return;

            const amount = Number(tx.amount) || 0;
            const day = d.getDate();
            const cat = tx.category || tx.categoryName || 'Other';

            dailyTotals[day] = (dailyTotals[day] || 0) + amount;

            if (!dailyCategories[day]) dailyCategories[day] = {};
            dailyCategories[day][cat] = (dailyCategories[day][cat] || 0) + amount;
        });

        const values = Object.values(dailyTotals);
        const maxValue = values.length ? Math.max(...values) : 0;

        container.className =
            'grid grid-cols-7 gap-[3px] sm:gap-1 p-2 bg-slate-50 dark:bg-slate-900 ' +
            'rounded-lg border border-slate-200 dark:border-slate-700';

        const dayNames = ['S', 'M', 'T', 'W', 'T', 'F', 'S'];
        dayNames.forEach(name => {
            const head = document.createElement('div');
            head.className =
                'text-[10px] sm:text-xs font-semibold text-slate-500 dark:text-slate-400 ' +
                'text-center mb-1';
            head.textContent = name;
            container.appendChild(head);
        });

        const firstDayOfWeek = new Date(year, month, 1).getDay();

        for (let i = 0; i < firstDayOfWeek; i++) {
            const empty = document.createElement('div');
            empty.className = 'aspect-square rounded-sm';
            empty.style.backgroundColor = 'transparent';
            container.appendChild(empty);
        }

        for (let day = 1; day <= daysInMonth; day++) {
            const value = dailyTotals[day] || 0;
            const div = document.createElement('div');

            div.className =
                'heatmap-cell aspect-square rounded-sm flex flex-col items-center justify-center ' +
                'text-[10px] leading-tight';
            div.style.backgroundColor = makeColor(value, maxValue);

            const amountLabel = formatShortAmount(value);
            div.innerHTML = `
                <span class="font-semibold">${day}</span>
                <span class="text-[12px] opacity-80">${amountLabel}</span>
            `;

            const displayDate = new Date(year, month, day);
            div.title = value
                ? `Hari ${displayDate.toLocaleDateString('id-ID', {
                      day: '2-digit',
                      month: 'long',
                      year: 'numeric'
                  })}: ${formatRupiah(value)}`
                : `Hari ${displayDate.toLocaleDateString('id-ID', {
                      day: '2-digit',
                      month: 'long',
                      year: 'numeric'
                  })}: tidak ada pengeluaran`;

            attachHover(div, {
                label: displayDate.toLocaleDateString('id-ID', {
                    day: '2-digit',
                    month: 'long',
                    year: 'numeric'
                }),
                total: value,
                categories: dailyCategories[day] || {}
            });

            container.appendChild(div);
        }

        const labelEl = document.getElementById('heatmapMonthLabel');
        if (labelEl) {
            const displayDate = new Date(year, month, 1);
            labelEl.textContent = displayDate.toLocaleDateString('id-ID', {
                month: 'long',
                year: 'numeric'
            });
        }

        return;
    }

    const weeklyTotals = {};
    const weeklyCategories = {};
    const monthlyTotals = {};
    const monthlyCategories = {};

    txList.forEach(tx => {
        if (!tx.date) return;
        const d = new Date(tx.date);
        if (isNaN(d.getTime())) return;
        if (d < minDate || d > maxDate) return;

        const typeRaw = (tx.type || tx.transactionType || '').toString().toUpperCase();
        if (typeRaw !== 'EXPENSE') return;

        const amount = Number(tx.amount) || 0;
        const cat = tx.category || tx.categoryName || 'Other';

        if (mode === 'weekly') {
            const dayOffset = Math.floor((d - minDate) / msPerDay);
            const weekIndex = Math.floor(dayOffset / 7);

            weeklyTotals[weekIndex] = (weeklyTotals[weekIndex] || 0) + amount;
            if (!weeklyCategories[weekIndex]) weeklyCategories[weekIndex] = {};
            weeklyCategories[weekIndex][cat] =
                (weeklyCategories[weekIndex][cat] || 0) + amount;
        } else if (mode === 'monthly') {
            const y = d.getFullYear();
            const m = d.getMonth();
            const key = `${y}-${String(m + 1).padStart(2, '0')}`;

            monthlyTotals[key] = (monthlyTotals[key] || 0) + amount;
            if (!monthlyCategories[key]) monthlyCategories[key] = {};
            monthlyCategories[key][cat] =
                (monthlyCategories[key][cat] || 0) + amount;
        }
    });

    if (mode === 'weekly') {
        const values = Object.values(weeklyTotals);
        const maxValue = values.length ? Math.max(...values) : 0;
        const totalWeeks = Math.floor((spanDays - 1) / 7) + 1;

        container.className =
            'grid grid-cols-7 sm:grid-cols-10 gap-[3px] sm:gap-1 p-2 bg-slate-50 dark:bg-slate-900 ' +
            'rounded-lg border border-slate-200 dark:border-slate-700';

        for (let w = 0; w < totalWeeks; w++) {
            const value = weeklyTotals[w] || 0;

            const start = new Date(minDate.getTime() + w * 7 * msPerDay);
            const end = new Date(start.getTime() + 6 * msPerDay);
            if (end > maxDate) end.setTime(maxDate.getTime());

            const div = document.createElement('div');
            div.className =
                'heatmap-cell aspect-square rounded-sm flex flex-col items-center justify-center ' +
                'text-[10px] leading-tight';
            div.style.backgroundColor = makeColor(value, maxValue);

            const amountLabel = formatShortAmount(value);
            div.innerHTML = `
                <span class="font-semibold">W${w + 1}</span>
                <span class="text-[12px] opacity-80">${amountLabel}</span>
            `;

            div.title = value
                ? `Minggu ${w + 1} (${start.toLocaleDateString('id-ID', {
                      day: '2-digit',
                      month: 'short'
                  })} – ${end.toLocaleDateString('id-ID', {
                      day: '2-digit',
                      month: 'short',
                      year: start.getFullYear() !== end.getFullYear() ? 'numeric' : undefined
                  })}): ${formatRupiah(value)}`
                : `Minggu ${w + 1} (${start.toLocaleDateString('id-ID', {
                      day: '2-digit',
                      month: 'short'
                  })} – ${end.toLocaleDateString('id-ID', {
                      day: '2-digit',
                      month: 'short',
                      year: start.getFullYear() !== end.getFullYear() ? 'numeric' : undefined
                  })}): tidak ada pengeluaran`;

            attachHover(div, {
                label: `Week ${w + 1} · ${start.toLocaleDateString('id-ID', {
                    day: '2-digit',
                    month: 'short'
                })} – ${end.toLocaleDateString('id-ID', {
                    day: '2-digit',
                    month: 'short'
                })}`,
                total: value,
                categories: weeklyCategories[w] || {}
            });

            container.appendChild(div);
        }
    } else {
        const values = Object.values(monthlyTotals);
        const maxValue = values.length ? Math.max(...values) : 0;

        container.className =
            'grid grid-cols-4 sm:grid-cols-6 gap-[3px] sm:gap-1 p-2 bg-slate-50 dark:bg-slate-900 ' +
            'rounded-lg border border-slate-200 dark:border-slate-700';

        let cursor = new Date(minDate.getFullYear(), minDate.getMonth(), 1);
        const lastMonth = new Date(maxDate.getFullYear(), maxDate.getMonth(), 1);

        while (cursor <= lastMonth) {
            const y = cursor.getFullYear();
            const m = cursor.getMonth();
            const key = `${y}-${String(m + 1).padStart(2, '0')}`;
            const value = monthlyTotals[key] || 0;

            const div = document.createElement('div');
            div.className =
                'heatmap-cell aspect-square rounded-sm flex flex-col items-center justify-center ' +
                'text-[10px] leading-tight';
            div.style.backgroundColor = makeColor(value, maxValue);

            const label = cursor.toLocaleDateString('id-ID', {
                month: 'short',
                year: '2-digit'
            });
            const amountLabel = formatShortAmount(value);

            div.innerHTML = `
                <span class="font-semibold">${label}</span>
                <span class="text-[12px] opacity-80">${amountLabel}</span>
            `;

            div.title = value
                ? `Bulan ${cursor.toLocaleDateString('id-ID', {
                      month: 'long',
                      year: 'numeric'
                  })}: ${formatRupiah(value)}`
                : `Bulan ${cursor.toLocaleDateString('id-ID', {
                      month: 'long',
                      year: 'numeric'
                  })}: tidak ada pengeluaran`;

            attachHover(div, {
                label: cursor.toLocaleDateString('id-ID', {
                    month: 'long',
                    year: 'numeric'
                }),
                total: value,
                categories: monthlyCategories[key] || {}
            });

            container.appendChild(div);

            cursor.setMonth(cursor.getMonth() + 1);
        }
    }

    const labelEl = document.getElementById('heatmapRangeLabel');
    if (labelEl) {
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
        labelEl.textContent = `Range: ${startLabel} – ${endLabel}`;
    }
});
