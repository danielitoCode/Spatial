<script lang="ts">
  import CodeBlock from '../../components/CodeBlock.svelte';
</script>

<section id="installation" class="py-24 bg-[#0A0E17] border-y border-[#1C2638]">
  <div class="max-w-3xl mx-auto px-6">
    <h2 class="text-3xl md:text-4xl font-bold text-center mb-12 text-[#e1e2ec]">Get Started</h2>

    <CodeBlock title="build.gradle.kts" lang="kotlin">
      dependencies {'{'}<br />
      &nbsp;&nbsp;implementation(<span class="text-[#19E6D2]">"io.github.danielitocode:spatial:0.1.0-alpha01"</span>)<br />
      &nbsp;&nbsp;<span class="text-[#6F7A90]">// or project(":spatial-compose") + runtime-adapter in-repo</span><br />
      {'}'}
    </CodeBlock>

    <div class="mt-8 text-center space-y-3">
      <p class="text-sm text-[#6F7A90]">Alpha release — API may change. Prefer the sample app under <span class="text-[#bacac6]">/app</span> while Core #1 stabilizes.</p>
      <p class="text-sm text-[#6F7A90]">
        <span class="text-[#bacac6] font-semibold">License:</span>
        <a href="https://github.com/danielitoCode/Spatial/blob/master/LICENSE" target="_blank" rel="noreferrer" class="text-[#19E6D2] hover:underline">PolyForm Shield 1.0.0</a>
        — free to use under your apps; not for competing libraries.
        © danielitoCode
      </p>
      <a href="#docs" class="inline-flex items-center gap-1.5 text-[#19E6D2] font-bold hover:underline text-sm">
        Full Documentation
        <span class="material-symbols-outlined text-base">arrow_forward</span>
      </a>
    </div>
  </div>
</section>
