<script lang="ts">
  import SpatialButton from '../../components/SpatialButton.svelte';

  interface Props {
    activeRoute?: string;
    onNavigate?: (route: string) => void;
  }

  let { activeRoute = 'home', onNavigate }: Props = $props();

  function navigate(e: MouseEvent, targetRoute: string) {
    // If it's a standard link, we don't necessarily need to prevent default
    // as long as the hashchange listener is active.
    // However, for the 'Open Studio' button which has no href, we need manual nav.
    if (onNavigate) {
      e.preventDefault();
      onNavigate(targetRoute);
    }
  }
</script>

<nav class="w-full top-0 sticky z-50 border-b border-[#1C2638] bg-[#05070D]/90 backdrop-blur-md">
  <div class="max-w-7xl mx-auto px-6 py-4 flex justify-between items-center">
    <!-- Brand Logo -->
    <a href="#home" onclick={(e) => navigate(e, 'home')} class="flex items-center gap-3 group">
      <img
        src="/public/spatial_icon_cleaned.svg"
        alt="Spatial Logo"
        class="w-15 h-15 object-contain group-hover:scale-105 transition-transform"
      />
      <span class="font-extrabold text-2xl tracking-tighter text-[#7effed]">SPATIAL</span>
    </a>

    <!-- Navigation Links -->
    <div class="hidden lg:flex items-center gap-7 text-sm font-medium">
      <a
        href="#home"
        onclick={(e) => navigate(e, 'home')}
        class="transition-colors pb-0.5 {activeRoute === 'home'
          ? 'text-[#7effed] font-bold border-b-2 border-[#19E6D2]'
          : 'text-[#bacac6] hover:text-[#7effed]'}"
      >
        Home
      </a>

      <a
        href="#docs"
        onclick={(e) => navigate(e, 'docs')}
        class="transition-colors pb-0.5 {activeRoute === 'docs'
          ? 'text-[#7effed] font-bold border-b-2 border-[#19E6D2]'
          : 'text-[#bacac6] hover:text-[#7effed]'}"
      >
        Documentation
      </a>

      <a
        href="#examples"
        onclick={(e) => navigate(e, 'examples')}
        class="transition-colors pb-0.5 {activeRoute === 'examples'
          ? 'text-[#7effed] font-bold border-b-2 border-[#19E6D2]'
          : 'text-[#bacac6] hover:text-[#7effed]'}"
      >
        Examples
      </a>

      <a
        href="#playground"
        onclick={(e) => navigate(e, 'playground')}
        class="transition-colors pb-0.5 flex items-center gap-1 {activeRoute === 'playground'
          ? 'text-[#7effed] font-bold border-b-2 border-[#19E6D2]'
          : 'text-[#bacac6] hover:text-[#7effed]'}"
      >
        <span class="material-symbols-outlined text-sm text-[#8B5CF6]">token</span>
        Playground
      </a>

      <a
        href="#roadmap"
        onclick={(e) => navigate(e, 'roadmap')}
        class="transition-colors pb-0.5 flex items-center gap-1 {activeRoute === 'roadmap'
          ? 'text-[#7effed] font-bold border-b-2 border-[#19E6D2]'
          : 'text-[#bacac6] hover:text-[#7effed]'}"
      >
        Roadmap
      </a>

      <a
        href="https://github.com/danielitoCode/Spatial"
        target="_blank"
        rel="noreferrer"
        class="text-[#bacac6] hover:text-[#7effed] transition-colors flex items-center gap-1"
      >
        GitHub
        <span class="material-symbols-outlined text-xs">open_in_new</span>
      </a>
    </div>

    <!-- Get Started CTA -->
    <SpatialButton variant="primary" size="sm" onclick={(e: MouseEvent) => navigate(e, 'playground')}>
      Open Studio
    </SpatialButton>
  </div>
</nav>
