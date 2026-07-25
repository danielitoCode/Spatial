<script lang="ts">
  import type { Snippet } from 'svelte';

  interface Props {
    tag?: string;
    class?: string;
    children?: Snippet;
  }

  let { tag = 'span', class: className = '', children }: Props = $props();
</script>

{#if tag === 'h1'}
  <h1 class="spatial-gradient-text {className}">
    {@render children?.()}
  </h1>
{:else if tag === 'h2'}
  <h2 class="spatial-gradient-text {className}">
    {@render children?.()}
  </h2>
{:else if tag === 'h3'}
  <h3 class="spatial-gradient-text {className}">
    {@render children?.()}
  </h3>
{:else}
  <span class="spatial-gradient-text {className}">
    {@render children?.()}
  </span>
{/if}
