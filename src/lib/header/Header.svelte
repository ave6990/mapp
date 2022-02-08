<script>
    import { page } from '$app/stores';
    import logo from './logo.svg';
</script>

<header>
    <div class="corner">
        <a href="https:#">
            <img src={logo} alt="Metrology App" />
        </a>
    </div>

    <nav>
        <div id="description">
            Метрологическая платформа
        </div>
        <ul>
            <li class:active={$page.url.pathname === '/'}>
                <a sveltekit:prefetch href="/">Главная</a>
            </li>
            <li class:active={$page.url.pathname === '/about'}>
                <a sveltekit:prefetch href="/about">О себе</a>
            </li>
        </ul>
    </nav>
</header>

<style>
    header {
        grid-column: 1 / 2;
        grid-row: 1;
        background: var(--primary-color);
        display: flex;
    }

    nav {
        color: var(--primary-text-color);
        height: 100%;
        display: grid;
        grid-template-columns: 1fr;
        grid-template-rows: 1fr 1fr;
    }

    div {
        margin: 0;
        padding: 0 0.5em;
    }

    ul {
        height: 100%;
        margin: 0;
        padding: 0 0 0 0.5em;
        display: flex;
        list-style: none;
    }

    li {
        margin: 0.2em 0;
        padding: 0.2em 0.5em;
        border-left: 0.2em solid var(--primary-text-color);
    }

    li a {
        color: var(--primary-text-color);
        text-decoration: none;
        font-size: 1.2em;
    }

    .active {
        background: var(--tertiary-color);
    }

    .corner a {
        display: flex;
        width: 4em;
        height: 4em;
    }

    .corner img {
        padding: 0;
        width: 4em;
        height: 4em;
    }
</style>
