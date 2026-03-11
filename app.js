const state = {
  tournament: { name: '', players: [], matches: [] },
};

const mockPlayers = ['Alex', 'Mira', 'Jon', 'Lena', 'Kai', 'Rin'];
mockPlayers.forEach((n) => state.tournament.players.push({ id: crypto.randomUUID(), name: n }));

const playersEl = document.getElementById('players');
const addBtn = document.getElementById('add-player');
const inputEl = document.getElementById('player-input');

function renderPlayers() {
  playersEl.innerHTML = '';
  state.tournament.players.forEach((p) => {
    const li = document.createElement('li');
    li.innerHTML = `${p.name} <button data-id="${p.id}">remove</button>`;
    li.querySelector('button').onclick = () => {
      state.tournament.players = state.tournament.players.filter((x) => x.id !== p.id);
      renderPlayers();
    };
    playersEl.appendChild(li);
  });
}

function generateMatches(players) {
  const matches = [];
  let round = 1;
  for (let i = 0; i < players.length; i++) {
    for (let j = i + 1; j < players.length; j++) {
      matches.push({ id: crypto.randomUUID(), a: players[i].id, b: players[j].id, round, result: null });
      round = (round % (players.length - 1)) + 1;
    }
  }
  return matches;
}

function applyResult(matchId, winsA, winsB) {
  const m = state.tournament.matches.find((x) => x.id === matchId);
  if (!m) return;
  m.result = { winsA, winsB, winner: winsA > winsB ? m.a : m.b };
  renderDashboard();
}

function standings() {
  const byId = new Map(state.tournament.players.map((p) => [p.id, { ...p, mw: 0, ml: 0, gw: 0, gl: 0 }]));
  state.tournament.matches.forEach((m) => {
    if (!m.result) return;
    const A = byId.get(m.a); const B = byId.get(m.b);
    A.gw += m.result.winsA; A.gl += m.result.winsB;
    B.gw += m.result.winsB; B.gl += m.result.winsA;
    if (m.result.winner === m.a) { A.mw++; B.ml++; } else { B.mw++; A.ml++; }
  });
  return [...byId.values()].sort((x, y) => y.mw - x.mw || y.gw - x.gw || x.name.localeCompare(y.name));
}

function renderDashboard() {
  document.getElementById('t-title').textContent = state.tournament.name;
  const completed = state.tournament.matches.filter((m) => m.result).length;
  document.getElementById('progress').textContent = `${completed}/${state.tournament.matches.length} matches completed`;

  const matchesEl = document.getElementById('matches');
  matchesEl.innerHTML = '';
  state.tournament.matches.forEach((m) => {
    const pa = state.tournament.players.find((p) => p.id === m.a).name;
    const pb = state.tournament.players.find((p) => p.id === m.b).name;
    const div = document.createElement('div');
    div.className = `match ${m.result ? 'done' : ''}`;
    div.innerHTML = `<strong>R${m.round}:</strong> ${pa} vs ${pb}`;
    if (!m.result) {
      const actions = document.createElement('div');
      actions.className = 'actions';
      [[2,0],[2,1],[1,2],[0,2]].forEach(([a,b]) => {
        const btn = document.createElement('button');
        btn.textContent = `${a}-${b}`;
        btn.onclick = () => applyResult(m.id,a,b);
        actions.appendChild(btn);
      });
      div.appendChild(actions);
    } else {
      const small = document.createElement('div');
      small.textContent = `Final: ${m.result.winsA}-${m.result.winsB}`;
      div.appendChild(small);
    }
    matchesEl.appendChild(div);
  });

  const tbody = document.getElementById('leaderboard');
  tbody.innerHTML = '';
  standings().forEach((p, i) => {
    const tr = document.createElement('tr');
    tr.innerHTML = `<td class="${i===0?'rank1':''}">${i+1}</td><td>${p.name}</td><td>${p.mw}</td><td>${p.ml}</td><td>${p.gw}</td><td>${p.gl}</td>`;
    tbody.appendChild(tr);
  });
}

addBtn.onclick = () => {
  const name = inputEl.value.trim();
  if (!name) return;
  if (state.tournament.players.some((p) => p.name.toLowerCase() === name.toLowerCase())) return alert('Duplicate name');
  state.tournament.players.push({ id: crypto.randomUUID(), name });
  inputEl.value = '';
  renderPlayers();
};

document.getElementById('start-btn').onclick = () => {
  if (state.tournament.players.length < 4) return alert('Need at least 4 players');
  state.tournament.name = document.getElementById('tournament-name').value || 'Draft Tournament';
  state.tournament.matches = generateMatches(state.tournament.players);
  document.getElementById('setup-panel').classList.add('hidden');
  document.getElementById('dashboard-panel').classList.remove('hidden');
  renderDashboard();
};

renderPlayers();
