import BrandPanel from "@/components/auth/BrandPanel";
import LoginForm from "@/components/auth/LoginForm";

export default function LoginPage() {
  return (
    <main className="flex min-h-screen w-full">
      <BrandPanel />
      <div className="flex w-full items-center justify-center px-6 py-12 md:w-1/2">
        <LoginForm />
      </div>
    </main>
  );
}