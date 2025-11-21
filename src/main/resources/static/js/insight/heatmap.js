// js/insight/daily-heatmap.js
// Aturan:
// - spanDays <= 31      -> calendar 1 bulan (daily)
// - 31 < spanDays <=180 -> weekly heatmap
// - spanDays > 180      -> monthly heatmap
// Fokus EXPENSE saja

document.addEventListener('DOMContentLoaded', function () {
    const container = document.getElementById('expenses-heatmap');
    if (!container) {
        console.log('[heatmap] container not found');
        return;
    }

    const txList = window.transactions || window.FINANCE_TRANSACTIONS || [];
    if (!txList || txList.length === 0) {
        console.log('[heatmap] no transactions');
        return;
    }

    const msPerDay = 24 * 60 * 60 * 1000;

    // --- cari min & max date dari transaksi EXPENSE ---
    let minDate = null;
    let maxDate = null;

    txList.forEach(tx => {
        if (!tx.date) return;

        const d = new Date(tx.date);
        if (isNaN(d.getTime())) return;

        const typeRaw = (tx.type || tx.transactionType || '').toString().toUpperCase();
        if (typeRaw !== 'EXPENSE') return;

        if (!minDate || d < minDate) minDate = d;
        if (!maxDate || d > maxDate) maxDate = d;
    });

    if (!minDate || !maxDate) {
        console.log('[heatmap] no expense dates found');
        return;
    }

    // normalize ke 00:00
    minDate = new Date(minDate.getFullYear(), minDate.getMonth(), minDate.getDate());
    maxDate = new Date(maxDate.getFullYear(), maxDate.getMonth(), maxDate.getDate());

    const spanDays = Math.floor((maxDate - minDate) / msPerDay) + 1;

    let mode;
    if (spanDays <= 31) {
        mode = 'dailyCalendar';
    } else if (spanDays <= 180) {
        mode = 'weekly';
    } else {
        mode = 'monthly';
    }

    console.log('[heatmap] mode =', mode, 'spanDays =', spanDays);

    // helper warna
    const baseAlpha = 0.25;
    function makeColor(value, maxValue) {
        if (!value || maxValue === 0) {
            return 'rgba(148, 163, 184, 0.15)'; // abu-abu tipis utk 0
        }
        const intensity = value / maxValue; // 0..1
        return `rgba(59, 130, 246, ${baseAlpha + intensity * 0.6})`;
    }

    // clear dulu
    container.innerHTML = '';

    // --- MODE 1: <= 31 hari -> kalender 1 bulan ---
    if (mode === 'dailyCalendar') {
        // pake bulan dari minDate (asumsi range 1 bulan)
        const year = minDate.getFullYear();
        const month = minDate.getMonth(); // 0-11
        const daysInMonth = new Date(year, month + 1, 0).getDate();

        // hitung total per hari di bulan tsb
        const dailyTotals = {}; // day(1..31) -> total

        txList.forEach(tx => {
            if (!tx.date) return;

            const d = new Date(tx.date);
            if (isNaN(d.getTime())) return;

            const typeRaw = (tx.type || tx.transactionType || '').toString().toUpperCase();
            if (typeRaw !== 'EXPENSE') return;

            if (d.getFullYear() !== year || d.getMonth() !== month) return;

            const day = d.getDate();
            const amount = Number(tx.amount) || 0;
            dailyTotals[day] = (dailyTotals[day] || 0) + amount;
        });

        const values = Object.values(dailyTotals);
        const maxValue = values.length ? Math.max(...values) : 0;

        // offset utk hari pertama (biar align sama Sun..Sat)
        const firstDayOfWeek = new Date(year, month, 1).getDay(); // 0 = Sun, 6 = Sat

        for (let i = 0; i < firstDayOfWeek; i++) {
            const empty = document.createElement('div');
            empty.className = 'aspect-square rounded-sm';
            empty.style.backgroundColor = 'transparent';
            container.appendChild(empty);
        }

        // render tanggal 1..N
        for (let day = 1; day <= daysInMonth; day++) {
            const value = dailyTotals[day] || 0;
            const div = document.createElement('div');
            div.className = 'aspect-square rounded-sm flex items-center justify-center text-xs';
            div.style.backgroundColor = makeColor(value, maxValue);
            div.textContent = day;

            const displayDate = new Date(year, month, day);
            div.title = value
                ? `Hari ${displayDate.toLocaleDateString('id-ID', {
                      day: '2-digit',
                      month: 'long',
                      year: 'numeric'
                  })}: ${value.toLocaleString('id-ID', {
                      style: 'currency',
                      currency: 'IDR',
                      maximumFractionDigits: 0
                  })}`
                : `Hari ${displayDate.toLocaleDateString('id-ID', {
                      day: '2-digit',
                      month: 'long',
                      year: 'numeric'
                  })}: tidak ada pengeluaran`;

            container.appendChild(div);
        }

        // label bulan
        const labelEl = document.getElementById('heatmapMonthLabel');
        if (labelEl) {
            const displayDate = new Date(year, month, 1);
            labelEl.textContent = displayDate.toLocaleDateString('id-ID', {
                month: 'long',
                year: 'numeric'
            });
        }

        return; // selesai mode calendar
    }

    // --- MODE 2 & 3: rebuild agregasi utk weekly / monthly ---

    const weeklyTotals = {};  // weekIndex -> total expense
    const monthlyTotals = {}; // yyyy-mm -> total expense

    txList.forEach(tx => {
        if (!tx.date) return;

        const d = new Date(tx.date);
        if (isNaN(d.getTime())) return;

        const typeRaw = (tx.type || tx.transactionType || '').toString().toUpperCase();
        if (typeRaw !== 'EXPENSE') return;

        if (d < minDate || d > maxDate) return;

        const amount = Number(tx.amount) || 0;

        if (mode === 'weekly') {
            const diffDays = Math.floor((d - minDate) / msPerDay);
            const weekIndex = Math.floor(diffDays / 7);
            weeklyTotals[weekIndex] = (weeklyTotals[weekIndex] || 0) + amount;
        } else if (mode === 'monthly') {
            const y = d.getFullYear();
            const m = d.getMonth();
            const key = `${y}-${String(m + 1).padStart(2, '0')}`;
            monthlyTotals[key] = (monthlyTotals[key] || 0) + amount;
        }
    });

    if (mode === 'weekly') {
        const values = Object.values(weeklyTotals);
        const maxValue = values.length ? Math.max(...values) : 0;

        const totalWeeks = Math.floor((spanDays - 1) / 7) + 1;

        for (let w = 0; w < totalWeeks; w++) {
            const value = weeklyTotals[w] || 0;

            const start = new Date(minDate.getTime() + w * 7 * msPerDay);
            const end = new Date(start.getTime() + 6 * msPerDay);
            if (end > maxDate) end.setTime(maxDate.getTime());

            const div = document.createElement('div');
            div.className = 'aspect-square rounded-sm flex items-center justify-center text-xs';
            div.style.backgroundColor = makeColor(value, maxValue);
            div.textContent = `W${w + 1}`;

            div.title = value
                ? `Minggu ${w + 1} (${start.toLocaleDateString('id-ID', {
                      day: '2-digit',
                      month: 'short'
                  })} – ${end.toLocaleDateString('id-ID', {
                      day: '2-digit',
                      month: 'short',
                      year: start.getFullYear() !== end.getFullYear() ? 'numeric' : undefined
                  })}): ${value.toLocaleString('id-ID', {
                      style: 'currency',
                      currency: 'IDR',
                      maximumFractionDigits: 0
                  })}`
                : `Minggu ${w + 1} (${start.toLocaleDateString('id-ID', {
                      day: '2-digit',
                      month: 'short'
                  })} – ${end.toLocaleDateString('id-ID', {
                      day: '2-digit',
                      month: 'short',
                      year: start.getFullYear() !== end.getFullYear() ? 'numeric' : undefined
                  })}): tidak ada pengeluaran`;

            container.appendChild(div);
        }
    } else {
        // mode === 'monthly'
        const values = Object.values(monthlyTotals);
        const maxValue = values.length ? Math.max(...values) : 0;

        let cursor = new Date(minDate.getFullYear(), minDate.getMonth(), 1);
        const lastMonth = new Date(maxDate.getFullYear(), maxDate.getMonth(), 1);

        while (cursor <= lastMonth) {
            const y = cursor.getFullYear();
            const m = cursor.getMonth();
            const key = `${y}-${String(m + 1).padStart(2, '0')}`;
            const value = monthlyTotals[key] || 0;

            const div = document.createElement('div');
            div.className = 'aspect-square rounded-sm flex items-center justify-center text-xs';
            div.style.backgroundColor = makeColor(value, maxValue);

            const label = cursor.toLocaleDateString('id-ID', {
                month: 'short',
                year: '2-digit'
            });
            div.textContent = label;

            div.title = value
                ? `Bulan ${cursor.toLocaleDateString('id-ID', {
                      month: 'long',
                      year: 'numeric'
                  })}: ${value.toLocaleString('id-ID', {
                      style: 'currency',
                      currency: 'IDR',
                      maximumFractionDigits: 0
                  })}`
                : `Bulan ${cursor.toLocaleDateString('id-ID', {
                      month: 'long',
                      year: 'numeric'
                  })}: tidak ada pengeluaran`;

            container.appendChild(div);

            cursor = new Date(y, m + 1, 1);
        }
    }

    // label atas untuk weekly & monthly
    const labelEl = document.getElementById('heatmapMonthLabel');
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
