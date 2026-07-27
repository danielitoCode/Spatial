<script lang="ts">
  import Navbar from './lib/features/navigation/Navbar.svelte';
  import Footer from './lib/features/navigation/Footer.svelte';
  import HeroSection from './lib/features/hero/HeroSection.svelte';
  import TechBar from './lib/features/tech-stack/TechBar.svelte';
  import ArchitectureSection from './lib/features/architecture/ArchitectureSection.svelte';
  import ComparisonSection from './lib/features/comparison/ComparisonSection.svelte';
  import CodePreviewSection from './lib/features/preview/CodePreviewSection.svelte';
  import FeaturesGridSection from './lib/features/features-grid/FeaturesGridSection.svelte';
  import InstallationSection from './lib/features/installation/InstallationSection.svelte';
  import GitHubCtaSection from './lib/features/cta/GitHubCtaSection.svelte';
  import ExamplesPage from './lib/features/examples/ExamplesPage.svelte';
  import PlaygroundPage from './lib/features/playground/PlaygroundPage.svelte';
  import DocsPage from './lib/features/docs/DocsPage.svelte';
  import RoadmapPage from './lib/features/roadmap/RoadmapPage.svelte';
  import CustomNavBar from "./lib/features/navigation/CustomNavBar.svelte";

  type Route = 'home' | 'examples' | 'playground' | 'docs' | 'roadmap';

  let currentRoute = $state<Route>('home');

  function updateRouteFromHash() {
    const hash = window.location.hash.replace('#', '');
    if (hash === 'examples' || hash === 'playground' || hash === 'docs' || hash === 'roadmap') {
      currentRoute = hash;
    } else {
      currentRoute = 'home';
    }
  }

  $effect(() => {
    // Initial check
    updateRouteFromHash();

    const handleHashChange = () => {
      updateRouteFromHash();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    window.addEventListener('hashchange', handleHashChange);
    return () => window.removeEventListener('hashchange', handleHashChange);
  });

  // Direct navigation helper that only updates the hash
  function handleNavigate(targetRoute: string) {
    if (window.location.hash === `#${targetRoute}`) {
      // If hash is same, hashchange won't fire, so scroll manually
      window.scrollTo({ top: 0, behavior: 'smooth' });
    } else {
      window.location.hash = targetRoute;
    }
  }
</script>

<div class="min-h-screen bg-[#05070D] text-[#e1e2ec] font-sans selection:bg-[#19E6D2] selection:text-[#00201c]">
  <!-- Top Navigation -->
  <!-- <Navbar activeRoute={currentRoute} onNavigate={handleNavigate} /> -->
  <CustomNavBar activeRoute={currentRoute} onNavigate={handleNavigate}/>

  <!-- Dynamic Feature Route Rendering -->
  <main>
    {#if currentRoute === 'home'}
      <HeroSection onNavigate={handleNavigate} />
      <TechBar />
      <ArchitectureSection />
      <ComparisonSection />
      <CodePreviewSection />
      <FeaturesGridSection />
      <InstallationSection />
      <GitHubCtaSection />
    {:else if currentRoute === 'examples'}
      <ExamplesPage />
    {:else if currentRoute === 'playground'}
      <PlaygroundPage />
    {:else if currentRoute === 'docs'}
      <DocsPage />
    {:else if currentRoute === 'roadmap'}
      <RoadmapPage />
    {/if}
  </main>

  <!-- Footer -->
  <Footer onNavigate={handleNavigate} />
</div>
