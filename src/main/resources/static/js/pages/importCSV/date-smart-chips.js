// js/pages/importCSV/date-smart-chips.js

document.addEventListener('DOMContentLoaded', function () {
  const form = document.getElementById('transactions-filter-form');
  const startInput =
    document.getElementById('startDate') ||
    document.querySelector('input[name="startDate"]');
  const endInput =
    document.getElementById('endDate') ||
    document.querySelector('input[name="endDate"]');

  const chips = document.querySelectorAll('.date-chip');

  if (!form || !startInput || !endInput || chips.length === 0) {
    console.log('[date-smart-chips] form / inputs / chips not found, skip');
    return;
  }

  const RANGE_KEYS = [
    'this-week',
    'last-7-days',
    'this-month',
    'last-month',
    'last-3-months',
    'ytd',
    'last-12-months',
    'all-time',
  ];

  const formatDate = (d) => {
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  };

  const startOfWeek = (date) => {
    // minggu mulai Senin
    const d = new Date(date);
    const day = d.getDay(); // 0=Sun,1=Mon,...
    const diff = day === 0 ? -6 : 1 - day;
    d.setDate(d.getDate() + diff);
    return d;
  };

  const startOfMonth = (date) => {
    const d = new Date(date);
    d.setDate(1);
    return d;
  };

  const endOfMonth = (date) => {
    const d = new Date(date);
    d.setMonth(d.getMonth() + 1, 0); // day 0 next month = last day this month
    return d;
  };

  const startOfYear = (date) => {
    const d = new Date(date);
    d.setMonth(0, 1); // Jan 1
    return d;
  };

  /**
   * Return { start, end } dalam format 'YYYY-MM-DD'
   * atau { start: '', end: '' } untuk 'all-time'
   */
  const getRangeDates = (rangeKey) => {
    const today = new Date();
    let start = null;
    let end = null;

    switch (rangeKey) {
      case 'this-week':
        start = startOfWeek(today);
        end = today;
        break;

      case 'last-7-days':
        end = today;
        start = new Date(today);
        start.setDate(start.getDate() - 6);
        break;

      case 'this-month':
        start = startOfMonth(today);
        end = today;
        break;

      case 'last-month': {
        const d = new Date(today);
        d.setMonth(d.getMonth() - 1);
        start = startOfMonth(d);
        end = endOfMonth(d);
        break;
      }

      case 'last-3-months': {
        end = today;
        start = new Date(today);
        start.setMonth(start.getMonth() - 3);
        break;
      }

      case 'ytd':
        start = startOfYear(today);
        end = today;
        break;

      case 'last-12-months':
        end = today;
        start = new Date(today);
        start.setFullYear(start.getFullYear() - 1);
        break;

      case 'all-time':
        return { start: '', end: '' };

      default:
        return null;
    }

    if (!start || !end) return null;

    return {
      start: formatDate(start),
      end: formatDate(end),
    };
  };

  const applyRangeToInputs = (rangeKey) => {
    const range = getRangeDates(rangeKey);
    if (!range) return;

    startInput.value = range.start;
    endInput.value = range.end;
  };

  const clearActiveClasses = () => {
    chips.forEach((chip) => {
      chip.classList.remove(
        'bg-blue-600',
        'text-white',
        'border-blue-600',
        'dark:border-blue-400'
      );
      chip.classList.remove('bg-blue-50', 'dark:bg-blue-900/40');

      chip.classList.add(
        'bg-gray-100',
        'dark:bg-slate-800',
        'border-gray-300',
        'dark:border-gray-600',
        'text-gray-700',
        'dark:text-gray-200'
      );
    });
  };

  const setActiveChipByKey = (rangeKey) => {
    clearActiveClasses();

    const chip = Array.from(chips).find(
      (c) => c.dataset.dateRange === rangeKey
    );
    if (!chip) return;

    chip.classList.remove(
      'bg-gray-100',
      'dark:bg-slate-800',
      'border-gray-300',
      'dark:border-gray-600',
      'text-gray-700',
      'dark:text-gray-200'
    );
    chip.classList.add(
      'bg-blue-600',
      'text-white',
      'border-blue-600',
      'dark:border-blue-400'
    );
  };

  /**
   * Baca nilai input date, cek apakah pas dengan salah satu preset
   * Kalau iya → nyalain chip-nya
   */
  const syncChipsFromInputs = () => {
    const currentStart = startInput.value || '';
    const currentEnd = endInput.value || '';

    // kalau dua2nya kosong → All time
    if (!currentStart && !currentEnd) {
      setActiveChipByKey('all-time');
      return;
    }

    // cek semua preset kecuali all-time
    let matchedKey = null;

    for (const key of RANGE_KEYS) {
      if (key === 'all-time') continue;

      const range = getRangeDates(key);
      if (!range) continue;

      if (range.start === currentStart && range.end === currentEnd) {
        matchedKey = key;
        break;
      }
    }

    if (matchedKey) {
      setActiveChipByKey(matchedKey);
    } else {
      // kalau ga ada yg match → ga ada chip aktif
      clearActiveClasses();
    }
  };

  // Event: klik chip → set input date + highlight chip + AUTO SUBMIT
  chips.forEach((chip) => {
    chip.addEventListener('click', () => {
      const key = chip.dataset.dateRange;
      if (!key) return;

      if (key === 'all-time') {
        startInput.value = '';
        endInput.value = '';
      } else {
        applyRangeToInputs(key);
      }

      setActiveChipByKey(key);

      // 🔥 auto submit di sini
      form.submit();
    });
  });

  // Event: manual change date → auto sync ke chip (TANPA submit)
  ['change', 'input'].forEach((evt) => {
    startInput.addEventListener(evt, syncChipsFromInputs);
    endInput.addEventListener(evt, syncChipsFromInputs);
  });

  // initial sync (pas halaman kebuka, misal filter dari backend)
  syncChipsFromInputs();
});
