<script lang="ts">
  interface Props {
    onNavigate?: (route: string) => void;
  }

  let { onNavigate }: Props = $props();

  const logoSrc = `${import.meta.env.BASE_URL}spatial_icon_cleaned.svg`;

  function navigate(e: MouseEvent, targetRoute: string) {
    if (onNavigate) {
      e.preventDefault();
      onNavigate(targetRoute);
    }
  }

  const socials = [
    { id: 'twitter', icon: 'https://cdn.simpleicons.org/x/19E6D2', link: '#' },
    { id: 'github', icon: 'https://cdn.simpleicons.org/github/19E6D2', link: 'https://github.com/danielitoCode/Spatial' },
    { id: 'discord', icon: 'https://cdn.simpleicons.org/discord/19E6D2', link: '#' }
  ];
</script>

<footer class="w-full pt-32 pb-12 bg-[#05070D] border-t border-white/5 relative overflow-hidden">
  <!-- Dynamic Background Ambient -->
  <div class="absolute -bottom-48 left-1/2 -translate-x-1/2 w-full max-w-5xl h-96 bg-primary/5 blur-[150px] rounded-full pointer-events-none opacity-50"></div>
  <div class="absolute top-0 left-0 w-full h-px bg-gradient-to-r from-transparent via-white/10 to-transparent"></div>

  <div class="max-w-7xl mx-auto px-6 grid grid-cols-1 md:grid-cols-12 gap-16 relative z-10">
    <!-- Brand Pillar -->
    <div class="md:col-span-4 space-y-8">
      <div class="flex items-center gap-4 group cursor-pointer" onclick={(e) => navigate(e, 'home')}>
        <div class="relative w-12 h-12">
            <div class="absolute inset-0 bg-primary blur-lg opacity-20 group-hover:opacity-40 transition-opacity"></div>
            <img src={logoSrc} alt="Spatial" class="w-full h-full object-contain relative z-10 transition-transform duration-500 group-hover:rotate-12" />
        </div>
        <div class="flex flex-col">
            <span class="font-black text-2xl text-white tracking-tighter italic uppercase leading-none">SPATIAL</span>
            <span class="text-[9px] font-mono text-primary font-black uppercase tracking-[0.3em] mt-1">0.1.0-alpha01</span>
        </div>
      </div>

      <p class="text-sm text-silver/40 leading-relaxed font-medium max-w-xs">
        Declarative 3D for Android with Jetpack Compose. OpenGL ES 3.0 under the hood — Core #1 still in active development.
      </p>

      <!-- Fixed Social Icons (Consistent with theme) -->
      <div class="flex items-center gap-4">
        {#each socials as social}
          <a
            href={social.link}
            target="_blank"
            rel="noreferrer"
            class="w-12 h-12 flex items-center justify-center rounded-2xl bg-white/[0.03] border border-white/[0.05] hover:border-primary/40 hover:bg-primary/10 transition-all duration-500 group shadow-lg"
          >
             <img src={social.icon} alt={social.id} class="w-5 h-5 opacity-70 group-hover:opacity-100 group-hover:scale-110 transition-all" />
          </a>
        {/each}
      </div>
    </div>

    <!-- Navigation Hub -->
    <div class="md:col-span-2 md:col-start-6 space-y-6">
      <h4 class="text-[10px] font-black text-white uppercase tracking-[0.3em] flex items-center gap-2">
          <div class="w-1 h-1 bg-primary rounded-full"></div>
          Platform
      </h4>
      <ul class="space-y-4 text-sm font-bold">
        <li><a href="#home" onclick={(e) => navigate(e, 'home')} class="text-silver/50 hover:text-white transition-colors flex items-center gap-2 group"><span class="w-1 h-1 rounded-full bg-white/10 group-hover:bg-primary transition-all"></span>Home</a></li>
        <li><a href="#docs" onclick={(e) => navigate(e, 'docs')} class="text-silver/50 hover:text-white transition-colors flex items-center gap-2 group"><span class="w-1 h-1 rounded-full bg-white/10 group-hover:bg-primary transition-all"></span>Documentation</a></li>
        <li><a href="#examples" onclick={(e) => navigate(e, 'examples')} class="text-silver/50 hover:text-white transition-colors flex items-center gap-2 group"><span class="w-1 h-1 rounded-full bg-white/10 group-hover:bg-primary transition-all"></span>Examples</a></li>
      </ul>
    </div>

    <div class="md:col-span-2 space-y-6">
      <h4 class="text-[10px] font-black text-white uppercase tracking-[0.3em] flex items-center gap-2">
          <div class="w-1 h-1 bg-secondary rounded-full"></div>
          Ecosystem
      </h4>
      <ul class="space-y-4 text-sm font-bold">
        <li><a href="#playground" onclick={(e) => navigate(e, 'playground')} class="text-silver/50 hover:text-white transition-colors flex items-center gap-2 group"><span class="w-1 h-1 rounded-full bg-white/10 group-hover:bg-primary transition-all"></span>Studio</a></li>
        <li><a href="#roadmap" onclick={(e) => navigate(e, 'roadmap')} class="text-silver/50 hover:text-white transition-colors flex items-center gap-2 group"><span class="w-1 h-1 rounded-full bg-white/10 group-hover:bg-primary transition-all"></span>Roadmap</a></li>
        <li><a href="https://github.com/danielitoCode/Spatial" target="_blank" rel="noreferrer" class="text-silver/50 hover:text-white transition-colors flex items-center gap-2 group"><span class="w-1 h-1 rounded-full bg-white/10 group-hover:bg-primary transition-all"></span>GitHub</a></li>
      </ul>
    </div>

    <!-- Newsletter / Beta -->
    <div class="md:col-span-3 space-y-6">
      <h4 class="text-[10px] font-black text-white uppercase tracking-[0.3em] flex items-center gap-2">
          <div class="w-1 h-1 bg-tertiary rounded-full"></div>
          Follow development
      </h4>
      <div class="glass-panel p-5 rounded-[2rem] border border-white/5 bg-white/[0.02] space-y-4 shadow-xl">
        <p class="text-[11px] text-silver/60 leading-relaxed font-medium">Track Core #1 device closure and Core #2 GLB work on <span class="text-white font-bold">GitHub</span>. Star the repo to get updates.</p>
        <a href="https://github.com/danielitoCode/Spatial" target="_blank" rel="noreferrer" class="block w-full text-center px-5 py-3 bg-primary text-[#00201c] rounded-2xl text-[10px] font-black uppercase hover:scale-[1.02] active:scale-95 transition-all">Open repository</a>
      </div>
    </div>
  </div>

  <!-- Bottom Legal Bar -->
  <div class="mt-24 pt-8 border-t border-white/5 max-w-7xl mx-auto px-6 flex flex-col sm:flex-row justify-between items-center gap-6 text-[10px] text-silver/20 font-mono uppercase tracking-[0.2em] font-black">
    <div class="flex items-center gap-6">
        <span>© 2026 SPATIAL · danielitoCode</span>
        <div class="w-px h-3 bg-white/5"></div>
        <span>OPEN SOURCE</span>
    </div>
    <div class="flex gap-10">
      <a href="https://github.com/danielitoCode/Spatial" target="_blank" rel="noreferrer" class="hover:text-primary transition-colors tracking-widest">GitHub</a>
      <a href="#docs" class="hover:text-primary transition-colors tracking-widest">Docs</a>
      <a href="#roadmap" class="hover:text-primary transition-colors tracking-widest">Roadmap</a>
    </div>
  </div>
</footer>
