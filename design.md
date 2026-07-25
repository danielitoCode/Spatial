# Spatial — Website Design Specification

> **SPATIAL**
>
> **3D RENDERING LIBRARY**

---

# 1. Project Identity

## Product Name

Spatial

## Product Category

3D Rendering Library for Android.

## Primary Ecosystem

- Android
- Kotlin
- Jetpack Compose

## Brand Positioning

Spatial is a modern, developer-focused 3D rendering library designed to bring declarative 3D experiences to Android applications.

The website must communicate:

- 3D
- Spatial computing
- Rendering
- Developer tooling
- Modern Android development
- Kotlin
- Declarative UI
- Performance
- Simplicity

The website should feel like the official website of a serious open-source developer library.

It must not look like:

- A generic SaaS landing page.
- A gaming website.
- A cryptocurrency website.
- A generic AI startup.
- A traditional corporate website.

The visual language must be strongly connected to:

- Real-time 3D rendering.
- Spatial environments.
- Geometry.
- Orbits.
- Depth.
- Perspective.
- Lighting.
- Materials.
- Technical visualization.

---

# 2. Brand Logo

The official Spatial logo is the primary visual identity.

The logo contains:

1. A central isometric cube.
2. A luminous orbital ring passing around the cube.
3. A small spherical object positioned on the orbital path.
4. A hexagonal outer structure surrounding the central object.
5. A cyan-to-blue-to-purple gradient.
6. The wordmark: `SPATIAL`
7. The slogan: `3D RENDERING LIBRARY`

The logo must be treated as a premium technical brand identity.

Do not redesign or alter the logo.

Do not replace the logo with a generic 3D cube icon.

Do not use the logo excessively.

Use the official logo in:

- Navigation header.
- Footer.
- Hero section when appropriate.
- Documentation branding.
- Open Graph / social preview if implemented.

---

# 3. Brand Visual Language

The visual identity is based on the following conceptual model:

```text
                SPATIAL
                   │
                   ▼
             3D SPACE
                   │
       ┌───────────┼───────────┐
       ▼           ▼           ▼
    Geometry    Rendering    Motion
       │           │           │
       └───────────┼───────────┘
                   ▼
          Declarative 3D
                   │
                   ▼
            Android + Kotlin
```

The website should visually communicate "a developer entering a 3D world".

The user should feel:

> "This is a serious 3D rendering library, and I can immediately see what it does."

---

# 4. Core Design Principles

## Principle 1 — 3D First

3D is not decoration.

3D is the primary visual language of the website.

Whenever possible, important sections should contain:

- Interactive 3D objects.
- Rotating geometry.
- Orbital systems.
- Depth.
- Perspective.
- Lighting.
- Reflections.
- Particles.
- Spatial transitions.

Avoid creating a website that is primarily flat 2D UI with occasional 3D images.

---

## Principle 2 — Developer First

The website exists primarily to help developers understand and adopt Spatial.

The visual experience must never make documentation difficult to access.

Important actions must always be easy to find:

- Get Started
- Documentation
- API Reference
- GitHub
- Examples
- Installation

---

## Principle 3 — Depth Over Decoration

Use depth intentionally.

Prefer:

- Layered surfaces.
- 3D objects.
- Perspective.
- Atmospheric gradients.
- Soft lighting.
- Spatial transitions.

Avoid:

- Excessive shadows.
- Random glassmorphism.
- Excessive glowing borders.
- Decorative gradients without purpose.

---

## Principle 4 — Technical Premium

The design should feel similar to a high-quality developer platform.

Visual references in terms of quality:

- Modern documentation platforms.
- Modern open-source project websites.
- Premium developer tools.
- Modern Android/Kotlin ecosystem.

The final design should feel polished but technically focused.

---

# 5. Color System

The primary brand gradient is:

```text
Cyan → Blue → Purple
```

Recommended gradient:

```css
--spatial-gradient:
  linear-gradient(
    135deg,
    #19E6D2 0%,
    #159FE8 48%,
    #8B5CF6 100%
  );
```

---

# 6. Brand Colors

## Primary Cyan

```text
Name: Spatial Cyan
HEX: #19E6D2
RGB: 25, 230, 210
```

Usage:

- Primary highlights.
- Active states.
- 3D lighting.
- Orbital effects.
- Interactive elements.

## Primary Blue

```text
Name: Spatial Blue
HEX: #159FE8
RGB: 21, 159, 232
```

Usage:

- Secondary brand color.
- Links.
- 3D surfaces.
- Gradient transitions.
- Interactive states.

## Primary Purple

```text
Name: Spatial Purple
HEX: #8B5CF6
RGB: 139, 92, 246
```

Usage:

- Secondary accents.
- Focus states.
- 3D lighting.
- End of brand gradients.
- Advanced / experimental features.

## Deep Space

```text
Name: Deep Space
HEX: #05070D
```

Primary dark background.

## Space Surface

```text
Name: Space Surface
HEX: #0A0E17
```

Used for elevated sections.

## Space Surface Elevated

```text
Name: Space Surface Elevated
HEX: #101624
```

Used for:

- Cards.
- Code panels.
- Documentation blocks.
- Interactive containers.

## Space Border

```text
Name: Space Border
HEX: #1C2638
```

Used for subtle borders.

## Primary Text

```text
Name: Space White
HEX: #F5F7FF
```

## Secondary Text

```text
Name: Space Silver
HEX: #A6B0C3
```

## Muted Text

```text
Name: Space Muted
HEX: #6F7A90
```

---

# 7. Dark Theme

Dark mode is the primary visual experience.

The dark theme should feel like a 3D viewport or a deep-space rendering environment.

```text
Background: #05070D
Surface: #0A0E17
Elevated Surface: #101624
Border: #1C2638

Primary Text: #F5F7FF
Secondary Text: #A6B0C3

Primary: #19E6D2
Secondary: #159FE8
Tertiary: #8B5CF6
```

---

# 8. Light Theme

The light theme must preserve the Spatial identity.

Do not simply invert the dark theme.

The light theme should feel like:

> A bright technical studio with subtle spatial depth.

```text
Background: #F7F9FC
Surface: #FFFFFF
Elevated Surface: #F0F4FA
Border: #DCE3EE

Primary Text: #0A0E17
Secondary Text: #4B5568
Muted Text: #718096

Primary: #089F94
Secondary: #087FC2
Tertiary: #7044D9
```

---

# 9. Semantic Color Tokens

The implementation should expose semantic tokens instead of hardcoding brand colors throughout the UI.

```text
background
backgroundElevated
surface
surfaceElevated
surfaceInteractive

textPrimary
textSecondary
textMuted

brandPrimary
brandSecondary
brandTertiary

border
borderInteractive

success
warning
error
info
```

Components should use semantic tokens.

Avoid using raw HEX values directly inside components.

---

# 10. Typography

The typography should be modern, technical, and highly readable.

Recommended primary font:

```text
Inter
```

Recommended code font:

```text
JetBrains Mono
```

Fallback:

```text
system-ui
sans-serif
```

---

# 11. Typography Scale

## Display

Used for the main hero title.

Characteristics:

- Large.
- Bold.
- Tight line height.
- High contrast.

Example:

```text
Build
3D experiences
for Android.
```

The words `3D` may use the Spatial gradient.

## H1

Large page titles.

## H2

Section titles.

## H3

Component titles.

## Body

Normal documentation and explanatory text.

## Code

Use JetBrains Mono.

Code should have:

- Syntax highlighting.
- Clear indentation.
- High contrast.
- Copy button.
- Language indicator.

---

# 12. Logo Usage

The logo should appear on dark backgrounds whenever possible.

The official logo contains:

```text
Cyan
Blue
Purple
```

The logo should never be placed inside a visually competing gradient.

Do not apply filters to the logo.

Do not recolor the logo.

---

# 13. Global Layout

The website uses a responsive centered layout.

Maximum content width:

```text
1280px
```

Documentation content width:

```text
1200px
```

Reading width:

```text
760px
```

Horizontal page padding:

```text
Mobile: 20px
Tablet: 32px
Desktop: 48px
Large Desktop: 64px
```

---

# 14. Spacing System

Use a consistent 4px base grid.

```text
4px
8px
12px
16px
20px
24px
32px
40px
48px
64px
80px
96px
128px
```

Large sections should use generous vertical spacing.

Avoid dense layouts.

---

# 15. Border Radius

Use moderate rounded corners.

```text
Small: 8px
Medium: 12px
Large: 16px
Extra Large: 24px
```

Cards:

```text
16px
```

Code blocks:

```text
12px
```

Buttons:

```text
10px
```

Avoid excessive pill-shaped UI.

Pills should be reserved for:

- Tags.
- Badges.
- Status indicators.

---

# 16. Navigation

The main navigation should be minimal.

Recommended structure:

```text
[ Spatial Logo ]

Documentation
API Reference
Examples
GitHub

[ Get Started ]
```

On mobile:

```text
[ Logo ]                       [ Menu ]
```

The navigation should remain visually lightweight.

The primary CTA should use the Spatial gradient.

---

# 17. Hero Section

The hero is the most important section of the website.

The hero should immediately communicate:

```text
Spatial
3D Rendering Library
for Android
```

Suggested structure:

```text
                    Navigation

                         ↓

                 [ 3D Visualizer ]

              Build 3D experiences
                    for Android

        A modern declarative 3D rendering
        library for the Android ecosystem.

        [ Get Started ] [ View on GitHub ]

                         ↓

             Kotlin / Android / Compose
```

---

# 18. Hero 3D Experience

The hero MUST contain an interactive or animated 3D scene.

The visual concept should be inspired by the official logo.

Scene:

```text
                    Hexagonal Cage

                         ╱╲
                       ╱    ╲
                      │ Cube │
                       ╲    ╱
                         ╲╱

                 Orbital Ring
                      ●
```

The scene should contain:

1. Central 3D cube.
2. Orbital ring.
3. Small sphere following the orbital path.
4. Hexagonal structural frame.

The objects should use:

```text
Cyan
Blue
Purple
```

with smooth gradients and physically-inspired lighting.

The cube should have visible depth.

The orbital ring should emit a subtle glow.

The sphere should act as a light source or emissive object.

The hexagonal frame should have subtle transparency or emissive edges.

The entire scene should rotate slowly.

User interaction:

- Mouse drag rotates the scene.
- Scroll changes camera distance.
- Touch drag rotates on mobile.
- Touch pinch controls zoom.

Interaction must remain subtle.

The 3D scene should never interfere with navigation or accessibility.

---

# 19. Hero Background

The hero background should use a deep-space environment.

Recommended:

```text
#05070D
```

Add extremely subtle:

- Radial gradients.
- Volumetric light.
- Grid fragments.
- Particle fields.

Do not create a distracting starfield.

The visual focus must remain on the Spatial 3D object.

---

# 20. Hero Gradient Text

Important words can use the brand gradient.

Example:

```text
Build
[3D experiences]
for Android.
```

Gradient:

```text
#19E6D2
→
#159FE8
→
#8B5CF6
```

Do not apply the gradient to every heading.

---

# 21. Trust / Technology Bar

Immediately below the hero, show a minimal technology bar.

Example:

```text
Kotlin       Android       Jetpack Compose       3D Rendering
```

The section should feel technical and understated.

---

# 22. What Is Spatial?

Create a section explaining the library.

Layout:

```text
┌──────────────────────┬────────────────────────┐
│                      │                        │
│  What is Spatial?    │       3D Scene        │
│                      │                        │
│  Explanation         │   Interactive Object   │
│                      │                        │
└──────────────────────┴────────────────────────┘
```

The 3D scene should visually represent:

```text
Composable
    ↓
Scene
    ↓
Objects
    ↓
Rendering
```

The section should explain the core concept without excessive technical complexity.

---

# 23. Declarative 3D Section

This is one of the most important sections.

The visual concept:

```text
Traditional 3D

Imperative
─────────────
Create
Add
Update
Remove
Transform


Spatial

Declarative
─────────────
Describe
Compose
Render
```

Use an animated transition between both concepts.

The Spatial side should be visually dominant.

The design should communicate that developers can describe a 3D scene in a declarative way.

---

# 24. Code + 3D Preview Section

Create an interactive split view.

Left:

```text
Kotlin code
```

Right:

```text
Live 3D rendering
```

Example:

```kotlin
SpatialScene {
    Cube(
        position = Position(0f, 0f, 0f)
    )

    Sphere(
        position = Position(2f, 0f, 0f)
    )
}
```

IMPORTANT: The exact API must be taken from the current Spatial repository.

Do not invent APIs that do not exist in the project.

The code example and 3D scene should be visually connected.

When the code changes:

```text
Code
  ↓
Scene
  ↓
3D Result
```

If live code editing is too complex, simulate the relationship with predefined examples.

---

# 25. Features Section

Features should be presented as visual 3D cards.

Possible categories:

```text
3D Primitives
Scene Composition
Transformations
Camera
Lighting
Materials
Animation
Declarative API
```

Only show features that are actually supported by the current Spatial implementation.

Do not claim unsupported capabilities.

Each card should contain a small 3D visual.

Example:

```text
┌────────────────────────────┐
│        [ 3D OBJECT ]       │
│                            │
│  3D Primitives             │
│                            │
│  Build scenes from         │
│  reusable 3D geometry.     │
└────────────────────────────┘
```

---

# 26. 3D Playground

The website should include a dedicated interactive 3D playground.

The playground should allow users to interact with a scene.

Controls:

```text
[ Rotate ]
[ Zoom ]
[ Reset ]

Object:
[ Cube ]
[ Sphere ]
[ Custom ]

Lighting:
[ Studio ]
[ Neon ]
[ Soft ]
```

The playground should visually demonstrate Spatial's capabilities.

The playground should not become a full 3D editor.

Keep it simple.

---

# 27. Documentation Section

Documentation must be treated as a first-class product.

Recommended navigation:

```text
Documentation

Getting Started
Installation
First Scene

Core Concepts
Scenes
Objects
Transforms
Camera
Lighting

API Reference

Examples

Advanced
Animation
Materials
Performance
```

The exact categories must reflect the actual project structure.

---

# 28. Installation Section

Provide a clear installation experience.

Example:

```text
Add Spatial to your project
```

Show:

```kotlin
dependencies {
    implementation("...")
}
```

Include:

```text
[ Copy ]
```

The dependency coordinate must always be generated from the current project/release configuration.

Never hardcode outdated versions in the website design specification.

---

# 29. Quick Start Section

Show the shortest possible path:

```text
1. Install Spatial

2. Create a scene

3. Add a 3D object

4. Run the application
```

Use a visual 3D timeline.

---

# 30. Examples Showcase

Create a horizontal 3D showcase.

Each example appears as a floating 3D card.

Examples may include:

```text
Basic Scene
Animated Cube
Multiple Objects
Camera Example
Lighting Example
Interactive Scene
```

Only include examples that exist or can actually be implemented.

Cards should have:

- Preview.
- Title.
- Short description.
- View example action.

---

# 31. Performance Section

The performance section should visually communicate:

```text
Efficient
Modern
Lightweight
Native
```

Use a 3D visualization of:

```text
Scene
    ↓
Scene Graph
    ↓
Renderer
    ↓
Android Device
```

Avoid fake benchmark numbers.

If performance metrics are not available, do not invent them.

---

# 32. Android Ecosystem Section

The website should visually associate Spatial with Android development.

Show:

```text
Kotlin
+
Jetpack Compose
+
Spatial
```

The visual should be a 3D pipeline.

Example:

```text
Kotlin
   ↓
Compose
   ↓
Spatial
   ↓
3D Scene
   ↓
Android
```

---

# 33. GitHub Section

The GitHub section should encourage developers to explore and contribute.

Include:

```text
Open Source
Built in Kotlin
Community Driven
```

CTA:

```text
[ View on GitHub ]
```

Use the official Spatial GitHub repository.

Do not fabricate GitHub statistics.

If statistics are displayed, retrieve them dynamically.

---

# 34. Community / Contribution

Create a section explaining how developers can participate.

Possible actions:

```text
Star the repository
Report an issue
Suggest a feature
Contribute code
Improve documentation
```

Only show actions that are supported by the repository.

---

# 35. Footer

Footer structure:

```text
SPATIAL

3D RENDERING LIBRARY

Documentation
API Reference
Examples
GitHub

Community
Issues
Contributing

© Spatial
```

The logo should appear in the footer.

The slogan should remain:

```text
3D RENDERING LIBRARY
```

---

# 36. 3D Visual System

All 3D visuals should follow a consistent style.

## Geometry

Prefer:

- Cubes.
- Spheres.
- Planes.
- Cylinders.
- Rings.
- Hexagonal structures.
- Abstract geometric compositions.

## Lighting

Use:

- Cyan key light.
- Blue fill light.
- Purple rim light.

Lighting should create depth.

---

# 37. Spatial 3D Logo Motif

The official logo's visual elements should become recurring design motifs.

## Cube

Represents:

```text
3D Geometry
```

## Orbital Ring

Represents:

```text
Motion
Spatial Relationships
Scene Interaction
```

## Sphere

Represents:

```text
Objects
Dynamic Elements
```

## Hexagonal Frame

Represents:

```text
Structure
Scene Graph
Framework
```

These motifs may appear throughout the website.

Do not copy the complete logo into every section.

Use individual motifs subtly.

---

# 38. 3D Background Elements

Some sections may contain subtle 3D background objects.

Examples:

```text
Floating cubes
Wireframe spheres
Orbital rings
Hexagonal grids
Particles
```

These must have low visual prominence.

Content always takes priority.

---

# 39. Motion Design

Motion should be slow and intentional.

Recommended:

```text
Hero object rotation:
12–30 seconds per revolution

Floating animation:
4–8 seconds

Micro interaction:
150–300ms

Section transition:
400–800ms
```

Avoid excessive motion.

Respect:

```text
prefers-reduced-motion
```

When reduced motion is enabled:

- Stop automatic rotation.
- Disable unnecessary particle movement.
- Use simple opacity transitions.
- Keep interaction functional.

---

# 40. Scroll Experience

The website should feel spatial as the user scrolls.

Recommended sequence:

```text
Hero
  ↓
3D Identity
  ↓
What is Spatial?
  ↓
Declarative 3D
  ↓
Code + 3D
  ↓
Features
  ↓
Playground
  ↓
Documentation
  ↓
Examples
  ↓
GitHub
  ↓
Footer
```

Some 3D objects may transition between sections.

Example:

```text
Hero Cube
    ↓
Transforms
    ↓
Splits into multiple objects
    ↓
Becomes a scene
```

This creates a visual narrative.

---

# 41. Light / Dark Theme Switching

The website must support:

```text
Dark
Light
System
```

Default:

```text
System preference
```

The user must be able to manually override the preference.

The 3D scene should adapt to the selected theme.

Dark:

```text
Deep space
High contrast
Neon lighting
```

Light:

```text
Bright studio
Soft shadows
Subtle cyan/blue/purple lighting
```

The 3D scene should not simply invert colors.

---

# 42. Responsive Design

The website must support:

```text
Mobile
Tablet
Desktop
Large Desktop
```

## Mobile

Prioritize:

```text
Logo
Navigation
Hero
Primary CTA
3D Scene
Documentation
```

3D scenes should remain performant.

On mobile:

- Reduce polygon complexity.
- Reduce particles.
- Reduce lighting complexity.
- Reduce animation frequency.
- Preserve the main visual identity.

---

# 43. Accessibility

The website must support:

- Keyboard navigation.
- Screen readers.
- Reduced motion.
- Sufficient color contrast.
- Focus states.
- Accessible buttons.
- Accessible navigation.

3D scenes must never be the only way to understand content.

Every important 3D visual should have a textual explanation.

---

# 44. Performance

Because Spatial itself is a 3D rendering library, the website must demonstrate technical quality.

The website should:

- Lazy-load heavy 3D scenes.
- Avoid loading all 3D assets immediately.
- Use optimized models.
- Use compressed textures.
- Prefer lightweight procedural geometry.
- Reduce rendering quality on low-power devices.
- Pause animations when sections are not visible.
- Use Intersection Observer where appropriate.
- Avoid unnecessary WebGL rendering.
- Respect reduced motion.

The hero 3D scene should load progressively.

The initial page should remain usable before the 3D renderer is fully ready.

---

# 45. 3D Technology

The website may use WebGL/WebGPU or an appropriate browser 3D technology.

The technology choice should prioritize:

1. Performance.
2. Mobile compatibility.
3. Visual quality.
4. Maintainability.
5. Accessibility.
6. Progressive enhancement.

Do not add a heavy 3D engine simply for decorative animations.

Use 3D technology where it adds meaningful value.

---

# 46. Progressive Enhancement

The website must remain usable if 3D rendering is unavailable.

Fallback:

```text
Interactive 3D
      ↓
Static 3D image
      ↓
Accessible visual placeholder
```

Core information must always remain available.

---

# 47. Code Block Design

Code blocks should use:

```text
Dark Surface
#101624
```

Border:

```text
#1C2638
```

Syntax highlighting should use the Spatial palette.

Example:

```text
Keywords     Purple
Functions    Cyan
Types        Blue
Strings      Green-ish Cyan
Comments     Muted Gray
Numbers      Purple
```

Do not overuse bright colors.

---

# 48. Buttons

## Primary

Use Spatial gradient.

```text
Cyan → Blue → Purple
```

Example:

```text
[ Get Started ]
```

## Secondary

Transparent or surface-based.

Example:

```text
[ View Documentation ]
```

## Ghost

Used for low-priority navigation.

---

# 49. Button Interaction

Hover:

- Slight elevation.
- Subtle glow.
- Small translation.

Active:

- Slight scale reduction.

Focus:

- Visible accessible focus ring.

Do not create excessive glow effects.

---

# 50. Cards

Cards should feel like floating surfaces in a 3D environment.

Use:

```text
Surface
Subtle border
Soft depth
```

On hover:

```text
translateY(-4px)
```

Optionally add:

```text
subtle 3D tilt
```

The tilt must be extremely subtle.

Do not rotate cards aggressively.

---

# 51. Documentation UX

Documentation should prioritize usability over visual effects.

When entering documentation:

- Reduce decorative 3D effects.
- Keep navigation persistent.
- Make search prominent.
- Make code examples easy to copy.
- Maintain readable content width.

The documentation experience should feel like a professional developer platform.

---

# 52. Search

The documentation should eventually support search.

Search should be accessible from:

```text
Desktop navigation
Documentation navigation
Keyboard shortcut
```

Suggested shortcut:

```text
⌘ K
Ctrl K
```

---

# 53. API Reference

The API Reference should be generated from the actual Spatial API whenever possible.

Do not manually duplicate API definitions.

The website should link to generated API documentation if available.

---

# 54. Content Rules

The AI implementing the website must never invent technical capabilities.

Before writing feature descriptions:

1. Inspect the Spatial repository.
2. Inspect the current source code.
3. Inspect README files.
4. Inspect examples.
5. Inspect documentation.
6. Verify API names.

If a feature does not exist:

Do not present it as available.

If a feature is planned:

Clearly label it:

```text
Planned
Experimental
Roadmap
```

Never represent roadmap functionality as production functionality.

---

# 55. Brand Voice

The copy should be:

- Technical.
- Confident.
- Clear.
- Concise.
- Developer-focused.

Avoid:

- Marketing buzzwords.
- Exaggerated claims.
- "Revolutionary".
- "Game-changing".
- "The world's best".

Prefer:

```text
Build 3D experiences for Android.

A modern declarative 3D rendering library
designed for the Android ecosystem.
```

---

# 56. Hero Copy

Primary recommendation:

```text
Build
3D experiences
for Android.
```

Supporting text:

```text
Spatial is a modern 3D rendering library
designed to bring declarative spatial experiences
to the Android ecosystem.
```

Primary CTA:

```text
Get Started
```

Secondary CTA:

```text
View on GitHub
```

---

# 57. Website Information Architecture

Recommended routes:

```text
/
├── /docs
│   ├── /getting-started
│   ├── /installation
│   ├── /first-scene
│   └── /concepts
│
├── /api
│
├── /examples
│
├── /playground
│
└── /github
```

The final routes must reflect the actual website implementation.

---

# 58. Homepage Structure

The homepage should follow:

```text
1. Navigation

2. Hero
   ├── 3D Spatial Logo Scene
   ├── Main Headline
   ├── Description
   └── CTAs

3. Technology Bar

4. What is Spatial?

5. Declarative 3D

6. Code + 3D Preview

7. Features

8. Interactive 3D Playground

9. Quick Start

10. Examples

11. Documentation

12. GitHub / Open Source

13. Final CTA

14. Footer
```

---

# 59. Final CTA

The final CTA should visually return to the logo's core 3D concept.

Example:

```text
Ready to build in 3D?

Start building with Spatial.

[ Get Started ]
[ View on GitHub ]
```

Behind the CTA:

A subtle animated cube with an orbital ring.

---

# 60. Final Design Direction

The final website must feel like:

```text
                    SPATIAL

        A 3D engine presented as a 3D experience.

        Modern
        Technical
        Spatial
        Declarative
        Android-native
        Developer-focused
```

The design should establish a strong visual connection between:

```text
Spatial Logo
      ↓
3D Geometry
      ↓
Spatial Relationships
      ↓
Declarative Scenes
      ↓
Android Development
```

The website should make the visitor understand the product within the first few seconds.

The primary visual idea is:

> "Spatial is a 3D rendering library, so the website itself should feel like entering a 3D scene."

---

# 61. Implementation Priority

When implementing the design, follow this priority:

## Priority 1

Brand identity.

## Priority 2

Hero 3D experience.

## Priority 3

Clear explanation of Spatial.

## Priority 4

Documentation and Getting Started.

## Priority 5

Code + 3D interactive demonstrations.

## Priority 6

Feature showcase.

## Priority 7

Playground.

## Priority 8

Performance and accessibility.

Visual effects must never compromise:

- Documentation.
- Navigation.
- Performance.
- Accessibility.
- Mobile usability.

---

# 62. AI Implementation Instructions

The AI responsible for implementing the website must:

1. Read this entire `design.md` before writing code.
2. Inspect the Spatial GitHub repository.
3. Understand the actual project architecture.
4. Identify the actual public API.
5. Use only verified features.
6. Use the uploaded official Spatial logo.
7. Preserve the Spatial brand identity.
8. Implement dark and light themes.
9. Implement responsive layouts.
10. Implement meaningful 3D visuals.
11. Use reusable UI components.
12. Avoid duplicated styles.
13. Centralize design tokens.
14. Optimize 3D assets.
15. Respect reduced motion.
16. Provide accessible fallbacks for 3D content.
17. Keep documentation easy to navigate.
18. Never fabricate API examples.
19. Never fabricate performance metrics.
20. Never claim unsupported features.

---

# 63. Definition of Done

The website is considered complete when:

- [ ] Spatial branding is consistent.
- [ ] Official logo is correctly displayed.
- [ ] "3D RENDERING LIBRARY" slogan is present.
- [ ] Dark theme implemented.
- [ ] Light theme implemented.
- [ ] System theme supported.
- [ ] Hero contains meaningful 3D content.
- [ ] Hero 3D scene is interactive.
- [ ] Website is responsive.
- [ ] Website has accessible navigation.
- [ ] Documentation is easy to access.
- [ ] Installation instructions are clear.
- [ ] Code examples are copyable.
- [ ] API documentation is connected to actual APIs.
- [ ] Examples are based on real Spatial capabilities.
- [ ] 3D playground is available or clearly planned.
- [ ] 3D content has fallbacks.
- [ ] Reduced-motion support exists.
- [ ] 3D assets are optimized.
- [ ] No unsupported features are advertised.
- [ ] No fabricated benchmarks are displayed.
- [ ] GitHub integration is present.
- [ ] Mobile experience is performant.
- [ ] The website visually communicates that Spatial is a 3D rendering library.

---

# 64. Core Visual Statement

The entire design system can be summarized as:

> **SPATIAL**
>
> **3D RENDERING LIBRARY**
>
> A deep-space inspired developer experience where geometry,
> motion, light, code, and declarative UI come together.

The website should not merely explain Spatial.

The website should **demonstrate Spatial's philosophy through its own design**.
