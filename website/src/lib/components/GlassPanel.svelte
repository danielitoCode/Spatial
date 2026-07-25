<script lang="ts">
  import type { Snippet } from 'svelte';

  interface Props {
    class?: string;
    glow?: boolean;
    children?: Snippet;
    [key: string]: any;
  }

  let { class: className = '', glow = false, children, ...restProps }: Props = $props();

  let panelRef = $state<HTMLElement | null>(null);

  function handleMouseMove(e: MouseEvent) {
    if (!panelRef) return;
    const rect = panelRef.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;
    panelRef.style.setProperty('--mouse-x', `${x}px`);
    panelRef.style.setProperty('--mouse-y', `${y}px`);
  }
</script>

<div
  bind:this={panelRef}
  onmousemove={handleMouseMove}
  class="glass-panel rounded-xl {glow ? 'glow-hover' : ''} {className}"
  {...restProps}
>
  {@render children?.()}
</div>

<style>
  div {
    position: relative;
    overflow: hidden;
  }
</style>
