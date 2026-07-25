<script lang="ts">
  interface Props {
    onNavigate?: (route: string) => void;
  }

  let { onNavigate }: Props = $props();

  function navigate(e: MouseEvent, targetRoute: string) {
    if (onNavigate) {
      e.preventDefault();
      onNavigate(targetRoute);
    }
  }
</script>

<footer class="w-full py-16 bg-[#0A0E17] border-t border-[#1C2638]">
  <div class="max-w-7xl mx-auto px-6 grid grid-cols-1 md:grid-cols-4 gap-10">
    <div class="space-y-4">
      <div class="flex items-center gap-3">
        <img
          src="https://lh3.googleusercontent.com/aida/AP1WRLv8F0fCCHN7e9LktQfoO8E1ON9THH6khQGintz1XEJCdZTC9Yb7Y_q-_IEbF2st7ODPfsZU4Ce9CodkEjF-TNdXzi3akxZGnaWt3f1q3cm4iTqBlW02RL_i_6K5nMVx0pHBbFEXju9i1p5YXcabu4ZCi5nxDAE6LF9t9bl_E0_MWCc8fzfaDe1gc2kj16KzruP_XNGR0SUvy8sqz6e--iN8P3OTmmdFH0vg8Udje7qcLeBUACKH1CEr55gv"
          alt="Spatial Logo"
          class="w-8 h-8 object-contain"
        />
        <span class="font-extrabold text-xl text-[#e1e2ec]">SPATIAL</span>
      </div>
      <p class="font-mono text-xs text-[#6F7A90] tracking-widest uppercase">3D RENDERING LIBRARY</p>
      <p class="text-sm text-[#6F7A90] leading-relaxed">
        Empowering Android developers with cinematic graphics and technical precision.
      </p>
    </div>

    <div>
      <h4 class="font-mono text-xs font-semibold text-[#e1e2ec] uppercase tracking-wider mb-4">Library</h4>
      <ul class="space-y-2.5 text-sm text-[#6F7A90]">
        <li><a href="#docs" onclick={(e) => navigate(e, 'docs')} class="hover:text-[#19E6D2] transition-colors">Documentation</a></li>
        <li><a href="#examples" onclick={(e) => navigate(e, 'examples')} class="hover:text-[#19E6D2] transition-colors">Showcase & Examples</a></li>
        <li><a href="#playground" onclick={(e) => navigate(e, 'playground')} class="hover:text-[#19E6D2] transition-colors">3D Playground Studio</a></li>
      </ul>
    </div>

    <div>
      <h4 class="font-mono text-xs font-semibold text-[#e1e2ec] uppercase tracking-wider mb-4">Community</h4>
      <ul class="space-y-2.5 text-sm text-[#6F7A90]">
        <li><a href="https://discord.gg" target="_blank" rel="noreferrer" class="hover:text-[#19E6D2] transition-colors">Discord</a></li>
        <li><a href="https://stackoverflow.com" target="_blank" rel="noreferrer" class="hover:text-[#19E6D2] transition-colors">Stack Overflow</a></li>
        <li><a href="#roadmap" onclick={(e) => navigate(e, 'roadmap')} class="hover:text-[#19E6D2] transition-colors">Project Roadmap</a></li>
      </ul>
    </div>

    <div>
      <h4 class="font-mono text-xs font-semibold text-[#e1e2ec] uppercase tracking-wider mb-4">Legal</h4>
      <ul class="space-y-2.5 text-sm text-[#6F7A90]">
        <li><a href="#home" onclick={(e) => navigate(e, 'home')} class="hover:text-[#19E6D2] transition-colors">Privacy Policy</a></li>
        <li><a href="https://www.apache.org/licenses/LICENSE-2.0" target="_blank" rel="noreferrer" class="hover:text-[#19E6D2] transition-colors">License (Apache 2.0)</a></li>
      </ul>
    </div>
  </div>

  <div class="mt-12 pt-8 border-t border-[#1C2638]/50 max-w-7xl mx-auto px-6 flex justify-between items-center text-xs text-[#6F7A90] font-mono">
    <span>© 2026 SPATIAL. ALL RIGHTS RESERVED.</span>
    <div class="flex gap-6">
      <a href="https://x.com" target="_blank" rel="noreferrer" class="hover:text-[#19E6D2] transition-colors">Twitter / X</a>
      <a href="https://linkedin.com" target="_blank" rel="noreferrer" class="hover:text-[#19E6D2] transition-colors">LinkedIn</a>
    </div>
  </div>
</footer>
