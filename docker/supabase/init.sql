create extension if not exists "pgcrypto";

create schema if not exists auth;

create table if not exists auth.users (
  instance_id uuid,
  id uuid primary key,
  aud varchar(255),
  role varchar(255),
  email varchar(255) unique,
  encrypted_password varchar(255),
  email_confirmed_at timestamptz,
  invited_at timestamptz,
  confirmation_token varchar(255),
  confirmation_sent_at timestamptz,
  recovery_token varchar(255),
  recovery_sent_at timestamptz,
  email_change_token_new varchar(255),
  email_change varchar(255),
  email_change_sent_at timestamptz,
  last_sign_in_at timestamptz,
  raw_app_meta_data jsonb,
  raw_user_meta_data jsonb,
  is_super_admin boolean,
  created_at timestamptz,
  updated_at timestamptz,
  phone text,
  phone_confirmed_at timestamptz,
  phone_change text default '',
  phone_change_token varchar(255) default '',
  phone_change_sent_at timestamptz,
  confirmed_at timestamptz generated always as (least(email_confirmed_at, phone_confirmed_at)) stored,
  email_change_token_current varchar(255) default '',
  email_change_confirm_status smallint default 0,
  banned_until timestamptz,
  reauthentication_token varchar(255) default '',
  reauthentication_sent_at timestamptz,
  is_sso_user boolean default false,
  deleted_at timestamptz,
  is_anonymous boolean default false
);

create table if not exists auth.refresh_tokens (
  instance_id uuid,
  id bigserial primary key,
  token varchar(255),
  user_id varchar(255),
  revoked boolean,
  created_at timestamptz,
  updated_at timestamptz
);

create table if not exists public.tournaments (
  id uuid primary key,
  user_id uuid not null,
  name text not null,
  status text not null,
  created_at timestamptz not null default now()
);

create table if not exists public.players (
  id uuid primary key,
  tournament_id uuid not null references public.tournaments(id) on delete cascade,
  name text not null
);

create table if not exists public.matches (
  id uuid primary key,
  tournament_id uuid not null references public.tournaments(id) on delete cascade,
  player_a_id uuid not null references public.players(id) on delete cascade,
  player_b_id uuid not null references public.players(id) on delete cascade,
  status text not null,
  wins_a integer not null default 0,
  wins_b integer not null default 0,
  winner_id uuid null references public.players(id) on delete set null
);

create role if not exists anon nologin;
create role if not exists authenticated nologin;
create role if not exists service_role nologin bypassrls;

grant usage on schema public to anon, authenticated, service_role;
grant all on all tables in schema public to anon, authenticated, service_role;
alter default privileges in schema public grant all on tables to anon, authenticated, service_role;
