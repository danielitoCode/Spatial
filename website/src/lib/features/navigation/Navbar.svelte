<script lang="ts">
  import SpatialButton from '../../components/SpatialButton.svelte';

  interface Props {
    activeRoute?: string;
    onNavigate?: (route: string) => void;
  }

  let { activeRoute = 'home', onNavigate }: Props = $props();

  const logoSrc = `${import.meta.env.BASE_URL}spatial_icon_cleaned.svg`;

  function navigate(e: MouseEvent, targetRoute: string) {
    if (onNavigate) {
      e.preventDefault();
      onNavigate(targetRoute);
    }
  }

  const navLinks = [
    { id: 'home', label: 'Home', icon: 'home' },
    { id: 'docs', label: 'Docs', icon: 'description' },
    { id: 'examples', label: 'Examples', icon: 'visibility' },
    { id: 'playground', label: 'Studio', icon: 'extension' },
    { id: 'roadmap', label: 'Roadmap', icon: 'timeline' }
  ];
</script>

<header class="fixed top-6 left-1/2 -translate-x-1/2 z-[100] w-[95%] max-w-6xl">
  <nav class="glass-panel-elevated p-2 rounded-[2rem] flex justify-between items-center border border-white/10 shadow-2xl shadow-black/80 transition-all duration-500 hover:border-white/20">
    <!-- Brand Logo Area -->
    <a
      href="#home"
      onclick={(e) => navigate(e, 'home')}
      class="flex items-center gap-3 pl-4 pr-6 py-2 group bg-white/5 rounded-[1.5rem] border border-white/5 hover:border-primary/30 transition-all duration-500"
    >
      <div class="relative w-9 h-9">
        <div class="absolute inset-0 bg-primary blur-lg opacity-20 group-hover:opacity-60 transition-opacity"></div>
        <img
          src={logoSrc}
          alt="Spatial Logo"
          class="w-full h-full object-contain relative z-10 group-hover:rotate-[360deg] transition-transform duration-1000"
        />
      </div>
      <div class="flex flex-col leading-none">
        <span class="font-black text-lg tracking-tighter text-white italic">SPATIAL</span>
        <span class="text-[8px] font-mono text-primary font-black uppercase tracking-[0.2em]">Core_v1.0</span>
      </div>
    </a>

    <!-- Main Navigation Links -->
    <div class="hidden lg:flex items-center gap-1.5 p-1.5 bg-black/40 rounded-[1.8rem] border border-white/5 backdrop-blur-3xl shadow-inner">
      {#each navLinks as link}
        <a
          href="#{link.id}"
          onclick={(e) => navigate(e, link.id)}
          class="group flex items-center gap-2 px-5 py-2.5 rounded-2xl text-[11px] font-black uppercase tracking-widest transition-all duration-500 {activeRoute === link.id
            ? 'bg-primary text-black shadow-lg shadow-primary/30 scale-105'
            : 'text-silver/50 hover:text-white hover:bg-white/5'}"
        >
          <span class="material-symbols-outlined text-[18px] transition-transform group-hover:scale-110 {activeRoute === link.id ? 'fill-current' : 'text-silver/30 group-hover:text-primary'}">
            {link.icon}
          </span>
          {link.label}
        </a>
      {/each}
    </div>

    <!-- Action Area -->
    <div class="flex items-center gap-3 pr-2">
      <a
        href="https://github.com/danielitoCode/Spatial"
        target="_blank"
        rel="noreferrer"
        class="hidden md:flex w-11 h-11 items-center justify-center rounded-2xl bg-white/5 border border-white/10 text-silver hover:text-white hover:border-primary/40 hover:bg-primary/10 transition-all duration-500 group"
        title="GitHub Repository"
      >
        <svg class="w-5 h-5 fill-current group-hover:scale-110 transition-transform" viewBox="0 0 24 24"><path d="M12 .297c-6.63 0-12 5.373-12 12 0 5.303 3.438 9.8 8.205 11.385.6.113.82-.258.82-.577 0-.285-.01-1.04-.015-2.04-3.338.724-4.042-1.61-4.042-1.61C4.422 18.07 3.633 17.7 3.633 17.7c-1.087-.744.084-.729.084-.729 1.205.084 1.838 1.236 1.838 1.236 1.07 1.835 2.809 1.305 3.495.998.108-.776.417-1.305.76-1.605-2.665-.3-5.466-1.332-5.466-5.93 0-1.31.465-2.38 1.235-3.22-.135-.303-.54-1.523.105-3.176 0 0 1.005-.322 3.3 1.23.96-.267 1.98-.399 3-.405 1.02.006 2.04.138 3 .405 2.28-1.552 3.285-1.23 3.285-1.23.645 1.653.24 2.873.12 3.176.765.84 1.23 1.91 1.23 3.22 0 4.61-2.805 5.625-5.475 5.92.42.36.81 1.096.81 2.22 0 1.606-.015 2.896-.015 3.286 0 .315.21.69.825.57C20.565 22.092 24 17.592 24 12.297c0-6.627-5.373-12-12-12"/></svg>
      </a>

      <SpatialButton
        variant="primary"
        size="sm"
        class="!rounded-2xl !px-6 h-11 shadow-2xl"
        onclick={(e: MouseEvent) => navigate(e, 'playground')}
      >
        <span class="material-symbols-outlined text-[20px]">rocket_launch</span>
        <span class="hidden sm:inline">LAUNCH STUDIO</span>
      </SpatialButton>
    </div>
  </nav>
</header>

<div class="h-28"></div> <!-- Spacer for fixed navbar -->
