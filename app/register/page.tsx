import BrandPanel from "@/components/auth/BrandPanel";
import RegisterForm from "@/components/auth/RegisterForm";

export default function RegisterPage() {
  return (
    <main className="flex min-h-screen w-full">
      <BrandPanel
        eyebrow="Start trading smarter"
        headline={
          <>
            Build your portfolio.
            <br />
            Learn the market.
          </>
        }
        description="Practice trading with real-time portfolio tracking, transaction history, and a balance that resets whenever you need a clean start."
      />

      <div className="flex w-full items-center justify-center px-6 py-12 lg:w-1/2 lg:px-12">
        <RegisterForm />
      </div>
    </main>
  );
}