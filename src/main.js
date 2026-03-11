import React, { useMemo, useState } from 'https://esm.sh/react@18.3.1';
import { createRoot } from 'https://esm.sh/react-dom@18.3.1/client';
import htm from 'https://esm.sh/htm@3.1.1';

const html = htm.bind(React.createElement);
const MOCK_PLAYERS = ['Alex', 'Mira', 'Jon', 'Lena', 'Kai', 'Rin'];
const RESULT_OPTIONS = [
  [2, 0],
  [2, 1],
  [1, 2],
  [0, 2],
];

const makePlayer = (name) => ({ id: crypto.randomUUID(), name });

function generateMatches(players) {
  const matches = [];
  let round = 1;

  for (let i = 0; i < players.length; i += 1) {
    for (let j = i + 1; j < players.length; j += 1) {
      matches.push({ id: crypto.randomUUID(), a: players[i].id, b: players[j].id, round, result: null });
      round = (round % (players.length - 1)) + 1;
    }
  }

  return matches;
}

function computeStandings(players, matches) {
  const byId = new Map(players.map((player) => [player.id, { ...player, mw: 0, ml: 0, gw: 0, gl: 0 }]));

  matches.forEach((match) => {
    if (!match.result) return;
    const playerA = byId.get(match.a);
    const playerB = byId.get(match.b);

    playerA.gw += match.result.winsA;
    playerA.gl += match.result.winsB;
    playerB.gw += match.result.winsB;
    playerB.gl += match.result.winsA;

    if (match.result.winner === match.a) {
      playerA.mw += 1;
      playerB.ml += 1;
    } else {
      playerB.mw += 1;
      playerA.ml += 1;
    }
  });

  return [...byId.values()]
    .map((row) => {
      const totalMatches = row.mw + row.ml;
      const totalGames = row.gw + row.gl;
      return {
        ...row,
        matchWinPct: totalMatches ? Math.round((row.mw / totalMatches) * 100) : 0,
        gameWinPct: totalGames ? Math.round((row.gw / totalGames) * 100) : 0,
      };
    })
    .sort(
      (left, right) =>
        right.mw - left.mw ||
        right.gw - left.gw ||
        right.gameWinPct - left.gameWinPct ||
        left.name.localeCompare(right.name),
    );
}

function groupMatchesByRound(matches) {
  const grouped = new Map();
  matches.forEach((match) => {
    if (!grouped.has(match.round)) grouped.set(match.round, []);
    grouped.get(match.round).push(match);
  });

  return [...grouped.entries()].sort((a, b) => a[0] - b[0]);
}

function App() {
  const [tournamentName, setTournamentName] = useState('Friday Night Draft');
  const [players, setPlayers] = useState(MOCK_PLAYERS.map(makePlayer));
  const [matches, setMatches] = useState([]);
  const [playerInput, setPlayerInput] = useState('');
  const [query, setQuery] = useState('');
  const [unfinishedOnly, setUnfinishedOnly] = useState(false);
  const [toast, setToast] = useState('');

  const inProgress = matches.length > 0;
  const completedMatches = useMemo(() => matches.filter((match) => match.result).length, [matches]);
  const standings = useMemo(() => computeStandings(players, matches), [players, matches]);
  const playersById = useMemo(() => new Map(players.map((player) => [player.id, player.name])), [players]);

  const filteredMatches = useMemo(() => {
    const trimmedQuery = query.trim().toLowerCase();

    return matches.filter((match) => {
      if (unfinishedOnly && match.result) return false;
      if (!trimmedQuery) return true;
      const aName = playersById.get(match.a)?.toLowerCase() ?? '';
      const bName = playersById.get(match.b)?.toLowerCase() ?? '';
      return aName.includes(trimmedQuery) || bName.includes(trimmedQuery);
    });
  }, [matches, playersById, query, unfinishedOnly]);

  const rounds = useMemo(() => groupMatchesByRound(filteredMatches), [filteredMatches]);
  const remainingMatches = useMemo(() => matches.filter((match) => !match.result), [matches]);

  function showToast(message) {
    setToast(message);
    setTimeout(() => setToast(''), 1600);
  }

  function addPlayer() {
    const name = playerInput.trim();
    if (!name) return;
    const duplicate = players.some((player) => player.name.toLowerCase() === name.toLowerCase());
    if (duplicate) {
      alert('Duplicate player name.');
      return;
    }

    setPlayers((current) => [...current, makePlayer(name)]);
    setPlayerInput('');
  }

  return html`
    <div className="app-shell">
      <header className="topbar">
        <h1>Tournamagic</h1>
        <p>Arcane Arena Draft Tracker</p>
      </header>

      <main className="layout">
        <section className="panel">
          <h2>Setup Tournament</h2>
          <label for="tournament-name">Tournament name</label>
          <input
            id="tournament-name"
            value=${tournamentName}
            onChange=${(event) => setTournamentName(event.target.value)}
            disabled=${inProgress}
          />

          <div className="row">
            <input
              placeholder="Add player"
              value=${playerInput}
              onChange=${(event) => setPlayerInput(event.target.value)}
              onKeyDown=${(event) => event.key === 'Enter' && addPlayer()}
              disabled=${inProgress}
              aria-label="Add player name"
            />
            <button type="button" onClick=${addPlayer} disabled=${inProgress}>Add</button>
          </div>

          <ul className="players-list">
            ${players.map(
              (player) => html`<li key=${player.id}>
                <span>${player.name}</span>
                <button
                  type="button"
                  onClick=${() => setPlayers((current) => current.filter((entry) => entry.id !== player.id))}
                  disabled=${inProgress}
                  aria-label=${`Remove ${player.name}`}
                >
                  Remove
                </button>
              </li>`,
            )}
          </ul>

          ${!inProgress
            ? html`<button
                type="button"
                className="primary"
                onClick=${() => {
                  if (players.length < 4) {
                    alert('Need at least 4 players.');
                    return;
                  }
                  setMatches(generateMatches(players));
                  showToast('Tournament started. Good luck, Planeswalkers.');
                }}
              >
                Start Tournament
              </button>`
            : html`<button
                type="button"
                className="danger"
                onClick=${() => {
                  if (!confirm('Reset tournament and clear all entered match results?')) return;
                  setMatches([]);
                  setQuery('');
                  setUnfinishedOnly(false);
                  showToast('Tournament reset.');
                }}
              >
                Reset Tournament
              </button>`}
        </section>

        ${inProgress &&
        html`<section className="panel">
          <div className="panel-header">
            <h2>${tournamentName || 'Draft Tournament'}</h2>
            <p className="chip" aria-live="polite">${completedMatches}/${matches.length} matches completed</p>
          </div>

          <div className="filters-row">
            <input
              placeholder="Filter by player"
              value=${query}
              onChange=${(event) => setQuery(event.target.value)}
              aria-label="Filter matches by player"
            />
            <label className="checkbox-row">
              <input
                type="checkbox"
                checked=${unfinishedOnly}
                onChange=${(event) => setUnfinishedOnly(event.target.checked)}
              />
              Unfinished only
            </label>
          </div>

          <div className="grid">
            <div>
              <h3>Matches</h3>
              <div className="match-list">
                ${rounds.length === 0
                  ? html`<p className="empty-state">No matches found for this filter.</p>`
                  : rounds.map(
                      ([roundNumber, roundMatches]) => html`<section key=${roundNumber} className="round-group">
                        <h4>Round ${roundNumber}</h4>
                        ${roundMatches.map((match) => {
                          const playerA = playersById.get(match.a);
                          const playerB = playersById.get(match.b);

                          return html`<article key=${match.id} className=${`match ${match.result ? 'done' : ''}`}>
                            <div className="match-title-row">
                              <strong>${playerA} vs ${playerB}</strong>
                              <span className=${`status-pill ${match.result ? 'done-pill' : 'open-pill'}`}>
                                ${match.result ? 'Completed' : 'Not started'}
                              </span>
                            </div>

                            ${!match.result
                              ? html`<div className="actions">
                                  ${RESULT_OPTIONS.map(
                                    ([winsA, winsB]) => html`<button
                                      type="button"
                                      key=${`${winsA}-${winsB}`}
                                      onClick=${() => {
                                        setMatches((current) =>
                                          current.map((entry) =>
                                            entry.id === match.id
                                              ? {
                                                  ...entry,
                                                  result: { winsA, winsB, winner: winsA > winsB ? entry.a : entry.b },
                                                }
                                              : entry,
                                          ),
                                        );
                                        showToast(`Saved result ${winsA}-${winsB} for ${playerA} vs ${playerB}.`);
                                      }}
                                    >
                                      ${winsA}-${winsB}
                                    </button>`,
                                  )}
                                </div>`
                              : html`<div className="result-row">
                                  <span>Final: ${match.result.winsA}-${match.result.winsB}</span>
                                  <button
                                    type="button"
                                    onClick=${() => {
                                      if (!confirm('Edit this result and reopen the match?')) return;
                                      setMatches((current) =>
                                        current.map((entry) =>
                                          entry.id === match.id ? { ...entry, result: null } : entry,
                                        ),
                                      );
                                      showToast(`Reopened ${playerA} vs ${playerB}.`);
                                    }}
                                  >
                                    Edit
                                  </button>
                                </div>`}
                          </article>`;
                        })}
                      </section>`,
                    )}
              </div>
            </div>

            <div>
              <h3>Leaderboard</h3>
              <table>
                <thead>
                  <tr>
                    <th>#</th>
                    <th>Player</th>
                    <th>MW</th>
                    <th>ML</th>
                    <th>GW</th>
                    <th>GL</th>
                    <th>MW%</th>
                    <th>GW%</th>
                  </tr>
                </thead>
                <tbody>
                  ${standings.map(
                    (player, index) => html`<tr key=${player.id}>
                      <td className=${index === 0 ? 'rank1' : ''}>${index + 1}</td>
                      <td>${player.name}</td>
                      <td>${player.mw}</td>
                      <td>${player.ml}</td>
                      <td>${player.gw}</td>
                      <td>${player.gl}</td>
                      <td>${player.matchWinPct}%</td>
                      <td>${player.gameWinPct}%</td>
                    </tr>`,
                  )}
                </tbody>
              </table>

              <h3>Remaining Matches</h3>
              ${remainingMatches.length === 0
                ? html`<p className="empty-state">All matches complete. Crown your champion.</p>`
                : html`<ul className="remaining-list">
                    ${remainingMatches.map((match) => html`<li key=${match.id}>
                      ${playersById.get(match.a)} vs ${playersById.get(match.b)}
                    </li>`)}</ul>`}
            </div>
          </div>
        </section>`}
      </main>

      ${toast && html`<div className="toast" role="status" aria-live="polite">${toast}</div>`}
    </div>
  `;
}

createRoot(document.getElementById('root')).render(html`<${App} />`);
