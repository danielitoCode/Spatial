<script lang="ts">
  import type { Snippet } from 'svelte';

  interface Props {
    variant?: 'primary' | 'secondary' | 'outline' | 'ghost';
    size?: 'sm' | 'md' | 'lg';
    href?: string;
    class?: string;
    children?: Snippet;
    onclick?: (e: MouseEvent) => void;
    [key: string]: any;
  }

  let {
    variant = 'primary',
    size = 'md',
    href,
    class: className = '',
    children,
    onclick,
    ...restProps
  }: Props = $props();

  const baseStyles =
    'font-bold rounded-lg transition-all duration-200 flex items-center justify-center gap-2 cursor-pointer active:scale-95';

  const variantStyles = {
    primary: 'spatial-gradient-bg text-[#00201c] glow-hover shadow-lg shadow-[#19E6D2]/10',
    secondary: 'bg-[#0A0E17] border border-[#1C2638] text-[#e1e2ec] hover:bg-[#1D1F26] hover:border-[#19E6D2]/40',
    outline: 'border border-[#19E6D2]/50 text-[#19E6D2] hover:bg-[#19E6D2]/10',
    ghost: 'text-[#bacac6] hover:text-[#7effed] hover:bg-white/5'
  };

  const sizeStyles = {
    sm: 'px-3 py-1.5 text-xs',
    md: 'px-5 py-2.5 text-sm',
    lg: 'px-7 py-3.5 text-base'
  };

  const computedClass = $derived(`${baseStyles} ${variantStyles[variant]} ${sizeStyles[size]} ${className}`);
</script>

{#if href}
  <a {href} class={computedClass} {...restProps}>
    {@render children?.()}
  </a>
{:else}
  <button type="button" {onclick} class={computedClass} {...restProps}>
    {@render children?.()}
  </button>
{/if}
