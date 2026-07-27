<script lang="ts">
  import type { Snippet } from 'svelte';

  interface Props {
    class?: string;
    glow?: boolean;
    elevated?: boolean;
    children?: Snippet;
    [key: string]: any;
  }

  let {
    class: className = '',
    glow = false,
    elevated = false,
    children,
    ...restProps
  }: Props = $props();

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
  class="{elevated ? 'glass-panel-elevated' : 'glass-panel'} rounded-2xl transition-all duration-500 {glow ? 'glow-hover' : ''} {className}"
  {...restProps}
>
  <!-- Spot reflection effect -->
  <div class="pointer-events-none absolute -inset-px opacity-0 transition duration-300 group-hover:opacity-100"
       style="background: radial-gradient(600px circle at var(--mouse-x) var(--mouse-y), rgba(25, 230, 210, 0.06), transparent 40%);">
  </div>

  <div class="relative z-10">
    {@render children?.()}
  </div>
</div>

<style>
  div {
    position: relative;
    overflow: hidden;
  }
</style>
