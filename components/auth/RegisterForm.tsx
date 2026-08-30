"use client";

import { useState, FormEvent } from "react";
import { useRouter } from "next/navigation";
import { registerUser } from "@/lib/api";

interface FieldErrors {
  name?: string;
  email?: string;
  password?: string;
  confirmPassword?: string;
}

export default function RegisterForm() {
  const router = useRouter();

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const [fieldErrors, setFieldErrors] = useState<FieldErrors>({});
  const [formError, setFormError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isSuccess, setIsSuccess] = useState(false);

  function validate(): FieldErrors {
    const errors: FieldErrors = {};

    if (!name.trim()) errors.name = "Enter your name.";
    if (!email.trim()) errors.email = "Enter your email.";
    if (!password) errors.password = "Enter a password.";
    if (!confirmPassword) errors.confirmPassword = "Confirm your password.";
    if (password && confirmPassword && password !== confirmPassword) {
      errors.confirmPassword = "Passwords don't match.";
    }

    return errors;
  }

  const isFormFilled =
    name.trim().length > 0 &&
    email.trim().length > 0 &&
    password.length > 0 &&
    confirmPassword.length > 0;

  async function handleSubmit(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setFormError(null);

    const errors = validate();
    setFieldErrors(errors);

    if (Object.keys(errors).length > 0 || isLoading) return;

    setIsLoading(true);
    try {
      await registerUser(name.trim(), email.trim(), password);
      setIsSuccess(true);
      setTimeout(() => router.push("/"), 900);
    } catch (err) {
      setFormError(
        err instanceof Error ? err.message : "Something went wrong. Please try again."
      );
      setIsLoading(false);
    }
  }

  return (
    <div className="w-full max-w-sm">
      <div className="mb-8 md:hidden">
        <span className="font-[family-name:var(--font-display)] text-xl font-semibold tracking-tight text-[var(--color-text)]">
          Trade<span className="text-[var(--color-accent)]">Sphere</span>
        </span>
      </div>

      <div className="rounded-2xl border border-[var(--color-border)] bg-[var(--color-surface)] p-8 shadow-[0_8px_40px_rgba(0,0,0,0.4)]">
        <h2 className="font-[family-name:var(--font-display)] text-2xl font-semibold text-[var(--color-text)]">
          Create your account
        </h2>
        <p className="mt-1.5 text-sm text-[var(--color-text-dim)]">
          Start practicing trades with a simulated portfolio and balance.
        </p>

        <form onSubmit={handleSubmit} className="mt-7 flex flex-col gap-5" noValidate>
          <Field
            id="name"
            label="Name"
            type="text"
            autoComplete="name"
            value={name}
            onChange={setName}
            placeholder="Jordan Rivera"
            error={fieldErrors.name}
          />

          <Field
            id="email"
            label="Email"
            type="email"
            autoComplete="email"
            value={email}
            onChange={setEmail}
            placeholder="you@example.com"
            error={fieldErrors.email}
          />

          <Field
            id="password"
            label="Password"
            type="password"
            autoComplete="new-password"
            value={password}
            onChange={setPassword}
            placeholder="••••••••"
            error={fieldErrors.password}
          />

          <Field
            id="confirmPassword"
            label="Confirm password"
            type="password"
            autoComplete="new-password"
            value={confirmPassword}
            onChange={setConfirmPassword}
            placeholder="••••••••"
            error={fieldErrors.confirmPassword}
          />

          {formError && (
            <div
              role="alert"
              className="rounded-lg border border-[var(--color-danger)]/30 bg-[var(--color-danger)]/10 px-3.5 py-2.5 text-sm text-[var(--color-danger)]"
            >
              {formError}
            </div>
          )}

          {isSuccess && (
            <div
              role="status"
              className="rounded-lg border border-[var(--color-accent)]/30 bg-[var(--color-accent)]/10 px-3.5 py-2.5 text-sm text-[var(--color-accent)]"
            >
              Account created — redirecting…
            </div>
          )}

          <button
            type="submit"
            disabled={!isFormFilled || isLoading || isSuccess}
            className="mt-1 flex items-center justify-center gap-2 rounded-lg bg-[var(--color-accent)] px-4 py-2.5 text-sm font-semibold text-[#06170f] transition-all hover:brightness-110 disabled:cursor-not-allowed disabled:opacity-40 disabled:hover:brightness-100 focus:outline-none focus-visible:ring-2 focus-visible:ring-[var(--color-accent-glow)] focus-visible:ring-offset-2 focus-visible:ring-offset-[var(--color-surface)]"
          >
            {isLoading ? (
              <>
                <span className="h-4 w-4 animate-spin rounded-full border-2 border-[#06170f]/30 border-t-[#06170f]" />
                Creating account…
              </>
            ) : (
              "Create account"
            )}
          </button>
        </form>

        <p className="mt-6 text-center text-sm text-[var(--color-text-dim)]">
          Already have an account?{" "}
          <a href="/" className="font-medium text-[var(--color-accent)] hover:underline">
            Sign in
          </a>
        </p>
      </div>
    </div>
  );
}

interface FieldProps {
  id: string;
  label: string;
  type: string;
  autoComplete: string;
  value: string;
  onChange: (value: string) => void;
  placeholder: string;
  error?: string;
}

function Field({ id, label, type, autoComplete, value, onChange, placeholder, error }: FieldProps) {
  return (
    <div className="flex flex-col gap-1.5">
      <label htmlFor={id} className="text-xs font-medium text-[var(--color-text-dim)]">
        {label}
      </label>
      <input
        id={id}
        name={id}
        type={type}
        autoComplete={autoComplete}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        placeholder={placeholder}
        aria-invalid={Boolean(error)}
        aria-describedby={error ? `${id}-error` : undefined}
        className={`rounded-lg border bg-[var(--color-surface-2)] px-3.5 py-2.5 text-sm text-[var(--color-text)] placeholder:text-[var(--color-text-faint)] outline-none transition-colors focus:ring-2 focus:ring-[var(--color-accent-glow)] ${
          error
            ? "border-[var(--color-danger)]/50 focus:border-[var(--color-danger)]"
            : "border-[var(--color-border)] focus:border-[var(--color-accent)]"
        }`}
      />
      {error && (
        <p id={`${id}-error`} className="text-xs text-[var(--color-danger)]">
          {error}
        </p>
      )}
    </div>
  );
}