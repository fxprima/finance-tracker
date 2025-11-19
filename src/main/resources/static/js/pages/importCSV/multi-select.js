function setupMultiSelect({ id }) {
  const hidden = document.getElementById(`${id}-hidden`);
  const wrapper = document.getElementById(`${id}-multi`);
  const input = document.getElementById(`${id}-input`);
  const chips = document.getElementById(`${id}-chips`);
  const dropdown = document.getElementById(`${id}-dropdown`);
  const clearBtn = document.getElementById(`${id}-clear`);
  const placeholder = document.getElementById(`${id}-placeholder`);

  if (!hidden || !wrapper || !input || !chips || !dropdown) return;

  const allOptions = Array.from(hidden.options).map(o => ({
    value: o.value,
    label: o.text
  }));

  const isSelected = (val) =>
    Array.from(hidden.selectedOptions).some(o => o.value === val);

  const selectedValues = () =>
    Array.from(hidden.selectedOptions).map(o => o.value);

  const syncPlaceholder = () => {
    const hasSel = selectedValues().filter(v => v !== '').length > 0;
    placeholder.classList.toggle('hidden', hasSel);
    clearBtn.disabled = !hasSel;
    clearBtn.classList.toggle('opacity-50', !hasSel);
  };

  const renderDropdown = (filter = "") => {
    dropdown.innerHTML = "";
    const q = filter.trim().toLowerCase();

    const items = allOptions.filter(
      o => o.label.toLowerCase().includes(q) && !isSelected(o.value)
    );

    if (items.length === 0) {
      dropdown.innerHTML =
        `<div class="px-3 py-2 text-sm text-gray-500">No results</div>`;
      return; // FIX: jangan lanjut
    }

    items.forEach(o => {
      const btn = document.createElement("button");
      btn.type = "button";
      btn.className = "w-full text-left px-3 py-2 text-sm hover:bg-gray-100";
      btn.textContent = o.label;
      btn.dataset.value = o.value;

      btn.addEventListener("click", (e) => {
        e.stopPropagation(); // FIX: biar tidak dianggap klik luar
        selectValue(o.value, o.label);
        input.value = "";
        renderDropdown("");
        input.focus();
      });

      dropdown.appendChild(btn);
    });
  };

  const addChip = (value, label) => {
    if (chips.querySelector(`[data-value="${CSS.escape(value)}"]`)) return;

    const chip = document.createElement("span");
    chip.dataset.value = value;
    chip.className = `w-full inline-flex items-center justify-between
        bg-blue-50 border border-blue-200
        text-blue-700 text-xs px-2 py-1 rounded-full`;

    chip.innerHTML = `
      <span>${label}</span>
      <button type="button"
        class="ml-2 leading-none rounded hover:bg-blue-100 px-1">&times;</button>
    `;

    chip.querySelector("button").addEventListener("click", (e) => {
      e.stopPropagation();
      removeValue(value);
    });

    chips.insertBefore(chip, input);
  };

  const selectValue = (value, label) => {
    const opt = Array.from(hidden.options).find(o => o.value === value);
    if (opt) opt.selected = true;
    else hidden.add(new Option(label, value, true, true));

    addChip(value, label);
    syncPlaceholder();
  };

  const removeValue = (value) => {
    Array.from(hidden.options).forEach(o => {
      if (o.value === value) o.selected = false;
    });

    const chip = chips.querySelector(`[data-value="${CSS.escape(value)}"]`);
    if (chip) chip.remove();

    renderDropdown(input.value);
    syncPlaceholder();
  };

  const clearAll = () => {
    Array.from(hidden.options).forEach(o => o.selected = false);
    chips.querySelectorAll("[data-value]").forEach(el => el.remove());
    input.value = "";
    renderDropdown("");
    syncPlaceholder();
  };

  wrapper.addEventListener("click", () => input.focus());

  input.addEventListener("focus", () => {
    dropdown.classList.remove("hidden");
    renderDropdown(input.value);
  });

  input.addEventListener("input", (e) => {
    dropdown.classList.remove("hidden");
    renderDropdown(e.target.value);
  });

  // FIX: klik item dropdown tidak menutup dropdown
  document.addEventListener("click", (e) => {
    if (!wrapper.contains(e.target)) dropdown.classList.add("hidden");
  });

  clearBtn.addEventListener("click", clearAll);

  // Prepopulate
  Array.from(hidden.selectedOptions).forEach(opt => {
    if (opt.value !== "") addChip(opt.value, opt.text);
  });

  syncPlaceholder();
}

setupMultiSelect({ id: 'category' });
setupMultiSelect({ id: 'sub-category' });
setupMultiSelect({ id: 'type' });
setupMultiSelect({ id: 'exclude-category' });
setupMultiSelect({ id: 'exclude-sub-category' });
setupMultiSelect({ id: 'exclude-type' });
