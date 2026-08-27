import AppShell from "@/components/layout/AppShell";
import { StoreProvider } from "@/context/StoreContext";

export default function StoreLayout({ children }) {
  return (
    <StoreProvider>
      <AppShell>{children}</AppShell>
    </StoreProvider>
  );
}
