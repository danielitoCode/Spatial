<script lang="ts">
  import type { Snippet } from 'svelte';

  interface Props {
    variant?: 'primary' | 'secondary' | 'outline' | 'ghost' | 'accent';
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
    'font-bold rounded-xl transition-all duration-300 flex items-center justify-center gap-2 cursor-pointer active:scale-95 group relative overflow-hidden';

  const variantStyles = {
    primary: 'spatial-gradient-bg text-[#00201c] shadow-lg shadow-primary/20 hover:shadow-primary/40 hover:-translate-y-0.5',
    secondary: 'bg-white/5 border border-white/10 text-white hover:bg-white/10 hover:border-white/20',
    outline: 'border border-primary/30 text-primary hover:bg-primary/10 hover:border-primary/60',
    ghost: 'text-silver/70 hover:text-white hover:bg-white/5',
    accent: 'bg-accent text-white shadow-lg shadow-accent/20 hover:shadow-accent/40'
  };

  const sizeStyles = {
    sm: 'px-4 py-2 text-xs',
    md: 'px-6 py-3 text-sm',
    lg: 'px-8 py-4 text-base tracking-tight'
  };

  const computedClass = $derived(`${baseStyles} ${variantStyles[variant]} ${sizeStyles[size]} ${className}`);
</script>

{#if href}
  <a {href} class={computedClass} {...restProps}>
    <!-- Shine effect on hover -->
    <div class="absolute inset-0 w-full h-full bg-gradient-to-r from-transparent via-white/20 to-transparent -translate-x-full group-hover:animate-[shine_1.5s_infinite]"></div>
    <span class="relative z-10 flex items-center gap-2">
      {@render children?.()}
    </span>
  </a>
{:else}
  <button type="button" {onclick} class={computedClass} {...restProps}>
    <div class="absolute inset-0 w-full h-full bg-gradient-to-r from-transparent via-white/20 to-transparent -translate-x-full group-hover:animate-[shine_1.5s_infinite]"></div>
    <span class="relative z-10 flex items-center gap-2">
      {@render children?.()}
    </span>
  </button>
{/if}

<style>
  @keyframes shine {
    100% {
      transform: translateX(100%);
    }
  }
</style>
