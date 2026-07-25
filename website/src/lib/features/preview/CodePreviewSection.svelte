<script lang="ts">
  import ComposeCodePane from './ComposeCodePane.svelte';
  import InteractiveViewport from './InteractiveViewport.svelte';
</script>

<section id="preview" class="py-24 bg-[#05070D]">
  <div class="max-w-7xl mx-auto px-6">
    <div class="grid lg:grid-cols-12 rounded-2xl overflow-hidden border border-[#1C2638] bg-[#0A0E17] shadow-2xl">
      <ComposeCodePane />
      <InteractiveViewport />
    </div>
  </div>
</section>
