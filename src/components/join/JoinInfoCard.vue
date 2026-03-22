<script setup lang="ts">
import JoinCardShell from '@/components/join/JoinCardShell.vue'
import { formatInvitationLabel } from '@/components/join/joinViewFormatters'
import type { JoinTournamentPreview } from '@/domain/models'

defineProps<{
  preview: JoinTournamentPreview
}>()
</script>

<template>
  <JoinCardShell title="Tournament Details" :meta="preview.status">
    <dl class="join-details">
      <div>
        <dt>Tournament</dt>
        <dd>{{ preview.tournamentName }}</dd>
      </div>

      <div>
        <dt>Join code</dt>
        <dd>{{ preview.joinCode }}</dd>
      </div>

      <div>
        <dt>Invitation</dt>
        <dd>{{ formatInvitationLabel(preview.joinEnabled) }}</dd>
      </div>
    </dl>

    <p v-if="!preview.joinEnabled" class="join-note join-note--warn">
      Joining is currently unavailable because this tournament is closed.
    </p>
    <p v-else class="join-note">
      Share this page with the right player, then claim the matching seat below.
    </p>
  </JoinCardShell>
</template>

<style scoped>
.join-details {
  margin: 0;
  display: grid;
  gap: 0.8rem;
}

.join-details div {
  display: grid;
  gap: 0.2rem;
}

.join-details dt,
.join-details dd,
.join-note {
  font-family: Cambria, "Palatino Linotype", Georgia, serif;
}

.join-details dt {
  color: #cfae7d;
  font-size: 0.82rem;
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.join-details dd {
  margin: 0;
  color: #f4e3bc;
  font-size: 1.02rem;
}

.join-note {
  margin: 1rem 0 0;
  color: #e3c99a;
  font-size: 0.95rem;
  line-height: 1.45;
}

.join-note--warn {
  color: #efb299;
}
</style>
