<script lang="ts">
  import type { Snippet } from 'svelte';

  interface Props {
    title?: string;
    lang?: string;
    class?: string;
    children?: Snippet;
  }

  let { title, lang = 'kotlin', class: className = '', children }: Props = $props();

  let copied = $state(false);

  function handleCopy() {
    copied = true;
    setTimeout(() => {
      copied = false;
    }, 2000);
  }
</script>

<div class="rounded-xl overflow-hidden border border-[#1C2638] bg-[#0A0E17] {className}">
  {#if title}
    <div class="flex items-center justify-between px-4 py-2.5 bg-[#171c28] border-b border-[#1C2638]">
      <div class="flex items-center gap-2">
        <div class="flex gap-1.5">
          <span class="w-2.5 h-2.5 rounded-full bg-red-500/80"></span>
          <span class="w-2.5 h-2.5 rounded-full bg-yellow-500/80"></span>
          <span class="w-2.5 h-2.5 rounded-full bg-green-500/80"></span>
        </div>
        <span class="ml-2 font-mono text-xs text-[#6F7A90]">{title}</span>
      </div>

      <button
        onclick={handleCopy}
        class="text-xs font-mono text-[#6F7A90] hover:text-[#19E6D2] transition-colors flex items-center gap-1"
      >
        <span class="material-symbols-outlined text-sm">{copied ? 'check' : 'content_copy'}</span>
        {copied ? 'Copied!' : 'Copy'}
      </button>
    </div>
  {/if}

  <div class="p-4 overflow-x-auto font-mono text-sm leading-relaxed text-[#e1e2ec]">
    {@render children?.()}
  </div>
</div>
