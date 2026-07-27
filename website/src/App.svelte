<script lang="ts">
  import { fly } from 'svelte/transition';
  import { cubicInOut } from 'svelte/easing';
  import { Canvas } from '@threlte/core';
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
  import GlobalParticles from "./lib/features/background/GlobalParticles.svelte";

  type Route = 'home' | 'docs' | 'examples' | 'playground' | 'roadmap';

  const routeOrder: Route[] = ['home', 'docs', 'examples', 'playground', 'roadmap'];

  let currentRoute = $state<Route>('home');
  let spinTrigger = $state(0);
  let spinDirection = $state<1 | -1>(1);

  function updateRouteFromHash() {
    const hash = (window.location.hash.replace('#', '') || 'home') as Route;
    if (routeOrder.includes(hash)) {
      if (currentRoute !== hash) {
        const prevIndex = routeOrder.indexOf(currentRoute);
        const currIndex = routeOrder.indexOf(hash);

        spinDirection = currIndex > prevIndex ? 1 : -1;
        spinTrigger++;
        currentRoute = hash;
      }
    }
  }

  $effect(() => {
    updateRouteFromHash();
    const handleHashChange = () => {
      updateRouteFromHash();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    };
    window.addEventListener('hashchange', handleHashChange);
    (window as any).difyChatbotConfig = { token: 'XZWJsDVSqxQtNsHH' };
    return () => window.removeEventListener('hashchange', handleHashChange);
  });

  function handleNavigate(targetRoute: string) {
    window.location.hash = targetRoute;
  }

  const slideDistance = 1000;
</script>

<svelte:head>
  <script src="https://udify.app/embed.min.js" id="XZWJsDVSqxQtNsHH" defer></script>
</svelte:head>

<div class="relative min-h-screen bg-[#05070D] text-[#e1e2ec] font-sans selection:bg-[#19E6D2] selection:text-[#00201c] overflow-x-hidden">

  <!-- BACKGROUND LAYER -->
  <div class="fixed inset-0 z-0 pointer-events-none">
    <Canvas>
      <GlobalParticles {spinTrigger} {spinDirection} />
    </Canvas>
  </div>

  <!-- RADIAL OVERLAY LAYER (SOFT BRUSH) -->
  <div class="fixed inset-0 z-[1] pointer-events-none bg-[radial-gradient(circle_at_50%_50%,rgba(5,7,13,0.5)_0%,transparent_80%)]"></div>

  <!-- NAVIGATION LAYER -->
  <CustomNavBar activeRoute={currentRoute} onNavigate={handleNavigate}/>

  <!-- CONTENT LAYER WITH SYNCHRONIZED SLIDE TRANSITIONS -->
  <main class="relative z-10 w-full min-h-screen">
    {#key currentRoute}
      <div
        class="w-full"
        in:fly={{ x: slideDistance * spinDirection, duration: 1000, easing: cubicInOut, opacity: 0 }}
        out:fly={{ x: -slideDistance * spinDirection, duration: 1000, easing: cubicInOut, opacity: 0 }}
      >
        <div class="w-full">
          {#if currentRoute === 'home'}
            <HeroSection onNavigate={handleNavigate} />
            <TechBar />
            <ArchitectureSection />
            <ComparisonSection />
            <CodePreviewSection />
            <FeaturesGridSection />
            <InstallationSection />
            <GitHubCtaSection />
          {:else if currentRoute === 'docs'}
            <DocsPage />
          {:else if currentRoute === 'examples'}
            <ExamplesPage />
          {:else if currentRoute === 'playground'}
            <PlaygroundPage />
          {:else if currentRoute === 'roadmap'}
            <RoadmapPage />
          {/if}

          <Footer onNavigate={handleNavigate} />
        </div>
      </div>
    {/key}
  </main>
</div>

<style>
  main {
    display: grid;
    grid-template-columns: 100%;
  }

  main > :global(div) {
    grid-area: 1 / 1 / 2 / 1;
  }

  /* Global Chatbot Overrides */
  :global(#dify-chatbot-bubble-button) {
    position: fixed !important;
    background: linear-gradient(135deg, #19E6D2 0%, #159FE8 100%) !important;
    width: 64px !important;
    height: 64px !important;
    border-radius: 24px !important;
    bottom: 30px !important;
    right: 30px !important;
    box-shadow: 0 10px 40px rgba(25, 230, 210, 0.4) !important;
    z-index: 999999 !important;
  }

  :global(#dify-chatbot-bubble-button::before) {
    content: '';
    position: absolute;
    width: 32px;
    height: 32px;
    background: url('/spatial_icon_cleaned.svg') no-repeat center;
    background-size: contain;
  }

  :global(#dify-chatbot-bubble-button svg) { display: none !important; }

  :global(#dify-chatbot-bubble-window) {
    position: fixed !important;
    border-radius: 2.8rem !important;
    background: #0D1117 !important;
    backdrop-filter: blur(25px) !important;
    z-index: 999999 !important;
    box-shadow: 0 30px 100px rgba(0, 0, 0, 0.9) !important;
  }
</style>
