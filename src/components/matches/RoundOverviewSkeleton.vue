<script setup lang="ts">
import SkeletonBlock from '@/components/shared/SkeletonBlock.vue'

const roundTabs = ['7.25rem', '7.1rem', '7.3rem', '7rem']
const matchRows = [
  { titleWidth: '56%', playerAWidth: '7.5rem', playerBWidth: '6.75rem' },
  { titleWidth: '61%', playerAWidth: '6.9rem', playerBWidth: '7.4rem' },
  { titleWidth: '52%', playerAWidth: '7.2rem', playerBWidth: '6.4rem' }
]
</script>

<template>
  <section class="matches-panel" aria-busy="true" aria-label="Loading round overview">
    <div class="matches-panel__heading">
      <div class="matches-panel__intro">
        <div class="matches-panel__copy">
          <SkeletonBlock width="10rem" height="1rem" />
          <div class="matches-panel__copy-lines">
            <SkeletonBlock width="19rem" height="0.95rem" />
            <SkeletonBlock width="13.5rem" height="0.95rem" />
          </div>
        </div>

        <div class="matches-panel__tabs" aria-hidden="true">
          <SkeletonBlock
            v-for="(tabWidth, index) in roundTabs"
            :key="index"
            :width="tabWidth"
            height="2.5rem"
            radius="0.75rem"
          />
        </div>
      </div>

      <div class="matches-panel__summary">
        <SkeletonBlock variant="light" width="7.5rem" height="1.7rem" />
        <div class="matches-panel__summary-lines">
          <SkeletonBlock variant="light" width="9rem" height="0.95rem" />
          <SkeletonBlock variant="light" width="6rem" height="0.95rem" />
        </div>
      </div>
    </div>

    <div class="matches-panel__content">
      <div class="matches-panel__list">
        <article v-for="(row, index) in matchRows" :key="index" class="match-row">
          <div class="match-row__title">
            <SkeletonBlock variant="light" :width="row.titleWidth" height="1.2rem" />
            <SkeletonBlock variant="light" width="5rem" height="0.95rem" />
          </div>

          <div class="match-row__arena">
            <div class="match-player">
              <SkeletonBlock circle width="64px" height="64px" />
              <div class="match-player__copy">
                <SkeletonBlock :width="row.playerAWidth" height="1rem" />
              </div>
            </div>

            <div class="match-score">
              <SkeletonBlock width="118px" height="3.65rem" radius="0.75rem" />
              <SkeletonBlock width="1rem" height="2rem" radius="0.25rem" />
              <SkeletonBlock width="118px" height="3.65rem" radius="0.75rem" />
            </div>

            <div class="match-player match-player--right">
              <div class="match-player__copy">
                <SkeletonBlock :width="row.playerBWidth" height="1rem" />
              </div>
              <SkeletonBlock circle width="64px" height="64px" />
            </div>
          </div>

          <div class="match-row__actions">
            <SkeletonBlock variant="light" width="188px" height="2.75rem" radius="0.75rem" />
            <SkeletonBlock variant="light" width="188px" height="2.75rem" radius="0.75rem" />
          </div>

          <div class="match-timer">
            <div class="timer-strip">
              <div class="timer-strip__meta">
                <SkeletonBlock width="6.5rem" height="1rem" />
                <SkeletonBlock width="4.75rem" height="0.9rem" />
                <div class="timer-controls">
                  <SkeletonBlock width="4rem" height="2rem" radius="0.65rem" />
                  <SkeletonBlock width="4rem" height="2rem" radius="0.65rem" />
                  <SkeletonBlock width="4rem" height="2rem" radius="0.65rem" />
                </div>
              </div>

              <div class="timer-strip__clock">
                <SkeletonBlock width="8.75rem" height="3.1rem" radius="0.5rem" />
                <div class="timer-strip__clock-note">
                  <SkeletonBlock width="6rem" height="0.9rem" />
                </div>
              </div>

              <div class="timer-strip__actions">
                <SkeletonBlock width="162px" height="2.5rem" radius="0.75rem" />
              </div>
            </div>
          </div>
        </article>
      </div>
    </div>
  </section>
</template>

<style scoped>
.matches-panel {
  border: 1px solid #875923;
  background:
    linear-gradient(180deg, rgba(71, 43, 24, 0.96), rgba(31, 18, 12, 0.98)),
    #26170f;
  padding: 0.95rem;
  display: grid;
  grid-template-rows: auto minmax(0, 1fr);
  gap: 0.95rem;
  min-height: 0;
  box-shadow:
    inset 0 0 0 1px rgba(222, 176, 94, 0.14),
    0 12px 22px rgba(0, 0, 0, 0.18);
}

.matches-panel__heading {
  display: grid;
  gap: 0.75rem;
}

.matches-panel__intro {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 1rem;
  align-items: center;
}

.matches-panel__copy,
.matches-panel__summary-lines,
.matches-panel__copy-lines,
.match-player__copy,
.timer-strip__meta,
.timer-strip__clock-note {
  display: grid;
}

.matches-panel__copy {
  gap: 0.35rem;
}

.matches-panel__copy-lines,
.matches-panel__summary-lines {
  gap: 0.3rem;
}

.matches-panel__tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 0.55rem;
  justify-content: flex-end;
}

.matches-panel__summary {
  border: 1px solid rgba(154, 108, 49, 0.62);
  background:
    linear-gradient(180deg, rgba(249, 228, 184, 0.92), rgba(220, 188, 129, 0.94)),
    #e2be83;
  color: #2d1705;
  padding: 0.95rem 1rem;
  display: grid;
  gap: 0.35rem;
}

.matches-panel__content {
  display: grid;
  grid-template-rows: minmax(0, 1fr);
  min-height: 0;
}

.matches-panel__list {
  display: grid;
  gap: 0.9rem;
  overflow: auto;
  min-height: 0;
  scrollbar-gutter: stable;
  padding-right: 0.2rem;
}

.match-row {
  border: 1px solid #8f5f27;
  background:
    linear-gradient(180deg, rgba(245, 223, 180, 0.96), rgba(222, 189, 131, 0.96)),
    #e2bf84;
  padding: 0.95rem;
  display: grid;
  gap: 0.75rem;
  box-shadow:
    inset 0 0 0 1px rgba(255, 239, 203, 0.18),
    0 12px 20px rgba(0, 0, 0, 0.16);
}

.match-row__title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 0.75rem;
}

.match-row__arena {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto minmax(0, 1fr);
  gap: 1rem;
  align-items: center;
  padding: 1rem;
  border: 1px solid #825724;
  background:
    linear-gradient(180deg, rgba(32, 31, 34, 0.95), rgba(18, 18, 20, 0.98)),
    #1c1c1e;
  box-shadow: inset 0 0 0 1px rgba(235, 191, 109, 0.08);
}

.match-player {
  display: flex;
  align-items: center;
  gap: 0.85rem;
  min-width: 0;
}

.match-player--right {
  justify-content: flex-end;
  text-align: right;
}

.match-player__copy {
  gap: 0.35rem;
}

.match-score {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.match-row__actions,
.timer-controls {
  display: flex;
  justify-content: center;
  gap: 1rem;
  flex-wrap: wrap;
}

.match-timer {
  border: 1px solid #7e5524;
  background:
    linear-gradient(180deg, rgba(52, 31, 19, 0.95), rgba(26, 15, 10, 0.98)),
    #24170f;
  box-shadow: inset 0 0 0 1px rgba(214, 171, 92, 0.12);
}

.timer-strip {
  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: center;
  gap: 1rem;
  padding: 0.85rem 1rem;
}

.timer-strip__meta {
  gap: 0.35rem;
  min-width: 180px;
}

.timer-controls {
  justify-content: flex-start;
  gap: 0.45rem;
  margin-top: 0.1rem;
}

.timer-strip__clock {
  display: grid;
  justify-items: center;
  gap: 0.35rem;
}

.timer-strip__clock-note {
  justify-items: center;
}

.timer-strip__actions {
  display: grid;
  justify-items: end;
}

@media (max-width: 1100px) {
  .matches-panel {
    padding: 0.8rem;
  }

  .matches-panel__intro {
    grid-template-columns: 1fr;
  }

  .matches-panel__tabs {
    justify-content: flex-start;
  }

  .match-row__arena {
    grid-template-columns: 1fr;
    justify-items: center;
  }

  .match-player,
  .match-player--right {
    width: 100%;
    justify-content: center;
    text-align: center;
  }
}

@media (max-width: 980px) {
  .timer-strip {
    grid-template-columns: 1fr;
    justify-items: center;
    text-align: center;
  }

  .timer-strip__meta,
  .timer-strip__actions {
    justify-items: center;
  }

  .timer-controls {
    justify-content: center;
  }
}

@media (max-width: 720px) {
  .match-score {
    gap: 0.55rem;
  }
}
</style>
