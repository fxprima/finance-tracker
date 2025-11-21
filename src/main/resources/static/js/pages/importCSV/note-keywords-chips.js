// js/pages/importCSV/note-keywords-chips.js

function initNoteKeywordsChips(config) {
  const { inputId, chipsId, hiddenId, emptyText } = config;

  const input = document.getElementById(inputId);
  const chipsContainer = document.getElementById(chipsId);
  const hiddenInput = document.getElementById(hiddenId);

  if (!input || !chipsContainer || !hiddenInput) {
    console.log(`[note-keywords] elements not found for ${inputId}, skip`);
    return;
  }

  // ✅ sekarang parse dari JSON, bukan split koma
  const parseHidden = () => {
    const raw = hiddenInput.value || "";
    if (!raw.trim()) return [];
    try {
      const arr = JSON.parse(raw);
      if (!Array.isArray(arr)) return [];
      return arr
        .map((k) => (typeof k === "string" ? k.trim() : ""))
        .filter((k) => k.length > 0);
    } catch (e) {
      console.warn("[note-keywords] failed to parse JSON, reset", e);
      return [];
    }
  };

  let keywords = parseHidden();

  // ✅ simpan ke hidden sebagai JSON array
  const syncHidden = () => {
    hiddenInput.value = JSON.stringify(keywords);
  };

  const renderChips = () => {
    chipsContainer.innerHTML = "";

    if (keywords.length === 0) {
      const empty = document.createElement("span");
      empty.className =
        "text-[11px] text-gray-400 dark:text-gray-500 italic";
      empty.textContent = emptyText || "";
      chipsContainer.appendChild(empty);
      return;
    }

    keywords.forEach((kw) => {
      const chip = document.createElement("span");
      chip.className =
        "inline-flex items-center px-2 py-1 rounded-full text-xs " +
        "bg-blue-50 border border-blue-200 text-blue-700 " +
        "dark:bg-blue-900/40 dark:border-blue-500/40 dark:text-blue-100 " +
        "max-w-full";

      chip.innerHTML = `
        <span class="truncate max-w-[180px]">${kw}</span>
        <button
          type="button"
          class="ml-1 text-xs leading-none rounded px-1
                 hover:bg-blue-100 dark:hover:bg-blue-800/60"
          aria-label="Remove keyword"
        >&times;</button>
      `;

      chip.querySelector("button").addEventListener("click", () => {
        keywords = keywords.filter((k) => k !== kw);
        syncHidden();
        renderChips();
        input.focus();
      });

      chipsContainer.appendChild(chip);
    });
  };

  input.addEventListener("keydown", (e) => {
    if (e.key === "Enter") {
      e.preventDefault();
      const value = (input.value || "").trim();
      if (!value) return;

      if (keywords.includes(value)) {
        input.value = "";
        return;
      }

      keywords.push(value);
      syncHidden();
      renderChips();
      input.value = "";
    }
  });

  // initial render
  renderChips();
}

document.addEventListener("DOMContentLoaded", function () {
  // INCLUDE notes keywords
  initNoteKeywordsChips({
    inputId: "note-keywords-input",
    chipsId: "note-keywords-chips",
    hiddenId: "note-keywords-hidden",
    emptyText: ""
  });

  // EXCLUDE notes keywords
  initNoteKeywordsChips({
    inputId: "exclude-note-keywords-input",
    chipsId: "exclude-note-keywords-chips",
    hiddenId: "exclude-note-keywords-hidden",
    emptyText: ""
  });
});
