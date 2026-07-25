<script lang="ts">
  import HeroSceneCanvas from './HeroSceneCanvas.svelte';
  import HeroContent from './HeroContent.svelte';

  interface Props {
    onNavigate?: (route: string) => void;
  }

  let { onNavigate }: Props = $props();
</script>

<header class="relative min-h-[90vh] flex items-center overflow-hidden bg-[#05070D]">
  <HeroSceneCanvas />
  <HeroContent {onNavigate} />
</header>
