<script>
    import Header from '$lib/header/Header.svelte'
    import Footer from '$lib/Footer.svelte'
    import '../app.css'
</script>

<Header />

<main>
    <slot />
</main>

<Footer />

<style>
    main {
        grid-column: 1 / 2;
        grid-row: 2;
        padding: 1rem;
        margin: 0 auto;
    }
</style>
