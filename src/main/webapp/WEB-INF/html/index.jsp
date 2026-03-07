<!DOCTYPE html>
<%@page pageEncoding="utf-8" contentType="text/html;charset=utf-8" session="true"%>
<html lang="en">
<head>
	<title>Browser Card Game</title>
	<meta charset="utf-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0" />
	<link rel="preconnect" href="https://fonts.googleapis.com">
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
	<link href="https://fonts.googleapis.com/css2?family=Orbitron:wght@400;700;900&family=Rajdhani:wght@300;400;500;600;700&display=swap" rel="stylesheet">
	<style>
		*, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

		:root {
			--gold: #d4a845;
			--gold-light: #f0d078;
			--gold-dim: #8a6d2b;
			--bg-deep: #07080c;
			--bg-card: rgba(13, 15, 25, 0.85);
			--border-subtle: rgba(212, 168, 69, 0.15);
			--border-glow: rgba(212, 168, 69, 0.4);
			--text-primary: #e8e4dc;
			--text-muted: #7a7668;
			--danger: #c0392b;
			--danger-bg: rgba(192, 57, 43, 0.12);
		}

		html, body {
			height: 100%;
			font-family: 'Rajdhani', sans-serif;
			background: var(--bg-deep);
			color: var(--text-primary);
			overflow: hidden;
		}

		/* --- Starfield background --- */
		.starfield {
			position: fixed;
			inset: 0;
			z-index: 0;
			overflow: hidden;
		}

		.starfield::before,
		.starfield::after {
			content: '';
			position: absolute;
			inset: 0;
		}

		.starfield::before {
			background:
				radial-gradient(1.5px 1.5px at 20% 30%, rgba(255,255,255,0.8), transparent),
				radial-gradient(1px 1px at 40% 70%, rgba(255,255,255,0.5), transparent),
				radial-gradient(1.5px 1.5px at 60% 20%, rgba(255,255,255,0.7), transparent),
				radial-gradient(1px 1px at 80% 50%, rgba(255,255,255,0.4), transparent),
				radial-gradient(1.2px 1.2px at 10% 80%, rgba(255,255,255,0.6), transparent),
				radial-gradient(1px 1px at 70% 85%, rgba(255,255,255,0.3), transparent),
				radial-gradient(1.5px 1.5px at 35% 10%, rgba(255,255,255,0.5), transparent),
				radial-gradient(1px 1px at 90% 15%, rgba(255,255,255,0.6), transparent),
				radial-gradient(1.2px 1.2px at 55% 55%, rgba(255,255,255,0.4), transparent),
				radial-gradient(1px 1px at 15% 45%, rgba(255,255,255,0.5), transparent),
				radial-gradient(1.5px 1.5px at 85% 75%, rgba(255,255,255,0.3), transparent),
				radial-gradient(1px 1px at 45% 95%, rgba(255,255,255,0.6), transparent),
				radial-gradient(1.2px 1.2px at 25% 60%, rgba(255,255,255,0.4), transparent),
				radial-gradient(1px 1px at 75% 40%, rgba(255,255,255,0.5), transparent),
				radial-gradient(1.5px 1.5px at 5% 5%, rgba(255,255,255,0.7), transparent),
				radial-gradient(1px 1px at 95% 90%, rgba(255,255,255,0.4), transparent);
			animation: drift 120s linear infinite;
		}

		.starfield::after {
			background:
				radial-gradient(1px 1px at 12% 22%, rgba(212,168,69,0.4), transparent),
				radial-gradient(1.2px 1.2px at 52% 42%, rgba(212,168,69,0.3), transparent),
				radial-gradient(1px 1px at 82% 62%, rgba(212,168,69,0.35), transparent),
				radial-gradient(1.5px 1.5px at 32% 82%, rgba(212,168,69,0.25), transparent),
				radial-gradient(1px 1px at 68% 8%, rgba(212,168,69,0.3), transparent);
			animation: drift 180s linear infinite reverse;
		}

		@keyframes drift {
			from { transform: translateY(0); }
			to { transform: translateY(-30px); }
		}

		/* --- Ambient glow --- */
		.ambient {
			position: fixed;
			inset: 0;
			z-index: 0;
			background:
				radial-gradient(ellipse 600px 400px at 20% 80%, rgba(212,168,69,0.04), transparent),
				radial-gradient(ellipse 500px 500px at 80% 20%, rgba(50,50,120,0.06), transparent);
			pointer-events: none;
		}

		/* --- Layout --- */
		.page {
			position: relative;
			z-index: 1;
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;
			min-height: 100vh;
			padding: 2rem;
		}

		/* --- Decorative lines --- */
		.deco-line {
			position: fixed;
			z-index: 0;
			background: linear-gradient(to bottom, transparent, var(--border-subtle), transparent);
		}
		.deco-line--left { left: 8%; top: 0; bottom: 0; width: 1px; }
		.deco-line--right { right: 8%; top: 0; bottom: 0; width: 1px; }
		.deco-line-h {
			position: fixed;
			z-index: 0;
			height: 1px;
			background: linear-gradient(to right, transparent, var(--border-subtle), transparent);
		}
		.deco-line-h--top { top: 12%; left: 0; right: 0; }
		.deco-line-h--bottom { bottom: 12%; left: 0; right: 0; }

		/* --- Header / Branding --- */
		.brand {
			text-align: center;
			margin-bottom: 3rem;
			animation: fadeUp 0.8s ease-out both;
		}

		.brand__icon {
			width: 56px;
			height: 56px;
			margin: 0 auto 1.25rem;
			position: relative;
		}

		.brand__icon svg {
			width: 100%;
			height: 100%;
		}

		.brand__title {
			font-family: 'Orbitron', sans-serif;
			font-size: clamp(1.5rem, 4vw, 2.4rem);
			font-weight: 900;
			letter-spacing: 0.2em;
			text-transform: uppercase;
			background: linear-gradient(135deg, var(--gold-light) 0%, var(--gold) 40%, var(--gold-dim) 100%);
			-webkit-background-clip: text;
			-webkit-text-fill-color: transparent;
			background-clip: text;
			line-height: 1.2;
		}

		.brand__sub {
			font-family: 'Rajdhani', sans-serif;
			font-size: 0.85rem;
			font-weight: 300;
			letter-spacing: 0.35em;
			text-transform: uppercase;
			color: var(--text-muted);
			margin-top: 0.5rem;
		}

		/* --- Card panel --- */
		.card {
			width: 100%;
			max-width: 400px;
			background: var(--bg-card);
			border: 1px solid var(--border-subtle);
			backdrop-filter: blur(20px);
			-webkit-backdrop-filter: blur(20px);
			padding: 2.5rem 2.25rem 2rem;
			position: relative;
			animation: fadeUp 0.8s ease-out 0.15s both;
		}

		/* Corner accents */
		.card::before,
		.card::after {
			content: '';
			position: absolute;
			width: 20px;
			height: 20px;
			border-color: var(--gold-dim);
			border-style: solid;
		}
		.card::before {
			top: -1px; left: -1px;
			border-width: 1px 0 0 1px;
		}
		.card::after {
			bottom: -1px; right: -1px;
			border-width: 0 1px 1px 0;
		}

		.card__corners-extra::before,
		.card__corners-extra::after {
			content: '';
			position: absolute;
			width: 20px;
			height: 20px;
			border-color: var(--gold-dim);
			border-style: solid;
		}
		.card__corners-extra::before {
			top: -1px; right: -1px;
			border-width: 1px 1px 0 0;
		}
		.card__corners-extra::after {
			bottom: -1px; left: -1px;
			border-width: 0 0 1px 1px;
		}

		/* --- Error banner --- */
		.error-banner {
			background: var(--danger-bg);
			border: 1px solid rgba(192, 57, 43, 0.3);
			color: #e74c3c;
			padding: 0.75rem 1rem;
			margin-bottom: 1.5rem;
			font-size: 0.9rem;
			font-weight: 500;
			letter-spacing: 0.04em;
			text-align: center;
			animation: shake 0.4s ease-out;
		}

		@keyframes shake {
			0%, 100% { transform: translateX(0); }
			20% { transform: translateX(-6px); }
			40% { transform: translateX(5px); }
			60% { transform: translateX(-3px); }
			80% { transform: translateX(2px); }
		}

		/* --- Form --- */
		.form__group {
			margin-bottom: 1.25rem;
		}

		.form__label {
			display: block;
			font-family: 'Orbitron', sans-serif;
			font-size: 0.65rem;
			font-weight: 700;
			letter-spacing: 0.2em;
			text-transform: uppercase;
			color: var(--gold-dim);
			margin-bottom: 0.5rem;
		}

		.form__input {
			width: 100%;
			padding: 0.8rem 1rem;
			background: rgba(255,255,255,0.03);
			border: 1px solid var(--border-subtle);
			color: var(--text-primary);
			font-family: 'Rajdhani', sans-serif;
			font-size: 1.05rem;
			font-weight: 400;
			letter-spacing: 0.04em;
			outline: none;
			transition: border-color 0.3s, box-shadow 0.3s, background 0.3s;
		}

		.form__input::placeholder {
			color: rgba(122, 118, 104, 0.5);
		}

		.form__input:focus {
			border-color: var(--border-glow);
			background: rgba(212, 168, 69, 0.03);
			box-shadow: 0 0 0 3px rgba(212, 168, 69, 0.06), inset 0 0 20px rgba(212, 168, 69, 0.02);
		}

		/* --- Submit button --- */
		.form__submit {
			width: 100%;
			margin-top: 0.75rem;
			padding: 0.85rem 2rem;
			font-family: 'Orbitron', sans-serif;
			font-size: 0.75rem;
			font-weight: 700;
			letter-spacing: 0.25em;
			text-transform: uppercase;
			color: var(--bg-deep);
			background: linear-gradient(135deg, var(--gold-light), var(--gold), var(--gold-dim));
			border: none;
			cursor: pointer;
			position: relative;
			overflow: hidden;
			transition: transform 0.2s, box-shadow 0.3s;
		}

		.form__submit::before {
			content: '';
			position: absolute;
			inset: 0;
			background: linear-gradient(135deg, transparent 30%, rgba(255,255,255,0.25) 50%, transparent 70%);
			transform: translateX(-100%);
			transition: transform 0.5s;
		}

		.form__submit:hover {
			transform: translateY(-1px);
			box-shadow: 0 6px 24px rgba(212, 168, 69, 0.25), 0 0 60px rgba(212, 168, 69, 0.08);
		}

		.form__submit:hover::before {
			transform: translateX(100%);
		}

		.form__submit:active {
			transform: translateY(0);
		}

		/* --- Divider --- */
		.divider {
			display: flex;
			align-items: center;
			gap: 1rem;
			margin: 1.5rem 0 0.5rem;
		}

		.divider__line {
			flex: 1;
			height: 1px;
			background: linear-gradient(to right, transparent, var(--border-subtle), transparent);
		}

		.divider__diamond {
			width: 6px;
			height: 6px;
			background: var(--gold-dim);
			transform: rotate(45deg);
			opacity: 0.5;
		}

		/* --- Footer --- */
		.footer {
			margin-top: 2rem;
			text-align: center;
			animation: fadeUp 0.8s ease-out 0.3s both;
		}

		.footer__text {
			font-size: 0.75rem;
			font-weight: 300;
			letter-spacing: 0.15em;
			color: var(--text-muted);
			opacity: 0.6;
		}

		/* --- Animations --- */
		@keyframes fadeUp {
			from {
				opacity: 0;
				transform: translateY(16px);
			}
			to {
				opacity: 1;
				transform: translateY(0);
			}
		}

		/* --- Responsive --- */
		@media (max-width: 480px) {
			.card {
				padding: 2rem 1.5rem 1.5rem;
			}
			.deco-line--left { left: 4%; }
			.deco-line--right { right: 4%; }
		}
	</style>
</head>
<body>

	<!-- Background layers -->
	<div class="starfield"></div>
	<div class="ambient"></div>

	<!-- Decorative lines -->
	<div class="deco-line deco-line--left"></div>
	<div class="deco-line deco-line--right"></div>
	<div class="deco-line-h deco-line-h--top"></div>
	<div class="deco-line-h deco-line-h--bottom"></div>

	<div class="page">

		<!-- Branding -->
		<header class="brand">
			<div class="brand__icon">
				<svg viewBox="0 0 56 56" fill="none" xmlns="http://www.w3.org/2000/svg">
					<!-- Stylized card fan icon -->
					<rect x="10" y="14" width="24" height="32" rx="2" stroke="url(#g1)" stroke-width="1.5" fill="none" transform="rotate(-12 22 30)" opacity="0.4"/>
					<rect x="18" y="10" width="24" height="32" rx="2" stroke="url(#g1)" stroke-width="1.5" fill="none" opacity="0.6"/>
					<rect x="26" y="14" width="24" height="32" rx="2" stroke="url(#g1)" stroke-width="1.5" fill="none" transform="rotate(12 38 30)" opacity="0.4"/>
					<!-- Center diamond -->
					<rect x="24" y="22" width="8" height="8" rx="1" fill="url(#g1)" transform="rotate(45 28 26)" opacity="0.7"/>
					<defs>
						<linearGradient id="g1" x1="0%" y1="0%" x2="100%" y2="100%">
							<stop offset="0%" stop-color="#f0d078"/>
							<stop offset="50%" stop-color="#d4a845"/>
							<stop offset="100%" stop-color="#8a6d2b"/>
						</linearGradient>
					</defs>
				</svg>
			</div>
			<h1 class="brand__title">Browser Card Game</h1>
			<p class="brand__sub">Online Card Game Assistance</p>
		</header>

		<!-- Login Card -->
		<div class="card">
			<div class="card__corners-extra"></div>

			<% if(request.getAttribute("reason") != null) { %>
			<div class="error-banner">
				<%= request.getAttribute("reason") %>
			</div>
			<% } %>

			<form action="login.htm" method="POST" autocomplete="on">
				<div class="form__group">
					<label class="form__label" for="email">Email</label>
					<input class="form__input" type="text" id="email" name="email" placeholder="Enter your email" autocomplete="email" required />
				</div>

				<div class="form__group">
					<label class="form__label" for="password">Password</label>
					<input class="form__input" type="password" id="password" name="password" placeholder="Enter your password" autocomplete="current-password" required />
				</div>

				<button class="form__submit" type="submit">Authenticate</button>
			</form>

			<div class="divider">
				<span class="divider__line"></span>
				<span class="divider__diamond"></span>
				<span class="divider__line"></span>
			</div>
		</div>

		<!-- Footer -->
		<footer class="footer">
			<p class="footer__text">SWLCG &middot; SWCCG</p>
		</footer>

	</div>

</body>
</html>
