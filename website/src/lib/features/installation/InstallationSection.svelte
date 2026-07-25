<script lang="ts">
  import CodeBlock from '../../components/CodeBlock.svelte';
</script>

<section id="installation" class="py-24 bg-[#0A0E17] border-y border-[#1C2638]">
  <div class="max-w-3xl mx-auto px-6">
    <h2 class="text-3xl md:text-4xl font-bold text-center mb-12 text-[#e1e2ec]">Get Started in Seconds</h2>

    <CodeBlock title="build.gradle.kts" lang="kotlin">
      dependencies {'{'}<br />
      &nbsp;&nbsp;implementation(<span class="text-[#19E6D2]">"io.spatial:rendering-core:1.2.0"</span>)<br />
      &nbsp;&nbsp;implementation(<span class="text-[#19E6D2]">"io.spatial:compose-ui:1.2.0"</span>)<br />
      &nbsp;&nbsp;implementation(<span class="text-[#19E6D2]">"io.spatial:assets-gltf:1.2.0"</span>)<br />
      {'}'}
    </CodeBlock>

    <div class="mt-8 text-center">
      <p class="text-sm text-[#6F7A90] mb-4">Already using Spatial? Check out the migration guide from v1.1.</p>
      <a href="#docs" class="inline-flex items-center gap-1.5 text-[#19E6D2] font-bold hover:underline text-sm">
        Full Documentation
        <span class="material-symbols-outlined text-base">arrow_forward</span>
      </a>
    </div>
  </div>
</section>
