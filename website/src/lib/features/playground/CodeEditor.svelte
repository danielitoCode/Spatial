<script lang="ts">
  interface Props {
    code: string;
    onCodeChange?: (newCode: string) => void;
    activeFile?: string;
    onSelectFile?: (file: string) => void;
  }

  let {
    code = $bindable(),
    onCodeChange,
    activeFile = 'AppScene.kt',
    onSelectFile
  }: Props = $props();

  const files = ['AppScene.kt', 'Materials.kt', 'Lights.kt'];

  function handleInput(e: Event) {
    const target = e.target as HTMLTextAreaElement;
    code = target.value;
    if (onCodeChange) {
      onCodeChange(code);
    }
  }

  const lineCount = $derived(code.split('\n').length);
</script>

<div class="h-full flex flex-col bg-[#0A0E17] rounded-xl overflow-hidden border border-[#1C2638]">
  <!-- File Tabs Bar -->
  <div class="flex items-center justify-between px-3 py-2 bg-[#101624] border-b border-[#1C2638] text-xs">
    <div class="flex gap-1 overflow-x-auto">
      {#each files as file}
        <button
          onclick={() => onSelectFile && onSelectFile(file)}
          class="px-3 py-1 rounded-md font-mono flex items-center gap-1.5 transition-colors {activeFile === file
            ? 'bg-[#0A0E17] text-[#19E6D2] border border-[#1C2638] font-bold'
            : 'text-[#6F7A90] hover:text-[#e1e2ec]'}"
        >
          <span class="material-symbols-outlined text-xs">code</span>
          {file}
        </button>
      {/each}
    </div>

    <span class="font-mono text-[10px] text-[#6F7A90] uppercase tracking-wider">Kotlin Compose DSL</span>
  </div>

  <!-- Editor Body -->
  <div class="flex-1 relative flex overflow-hidden font-mono text-xs leading-relaxed">
    <!-- Line Numbers -->
    <div class="w-10 py-3 bg-[#05070D] border-r border-[#1C2638] text-right pr-2 select-none text-[#6F7A90]/60">
      {#each Array(lineCount) as _, i}
        <div>{i + 1}</div>
      {/each}
    </div>

    <!-- Textarea Code Input -->
    <textarea
      value={code}
      oninput={handleInput}
      spellcheck="false"
      class="w-full h-full p-3 bg-transparent text-[#e1e2ec] resize-none outline-none border-none font-mono focus:ring-0 leading-relaxed selection:bg-[#19E6D2]/20"
    ></textarea>
  </div>
</div>
