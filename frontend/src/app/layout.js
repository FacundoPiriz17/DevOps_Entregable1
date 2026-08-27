import "./globals.css";
import Providers from "./providers";

export const metadata = {
  title: {
    default: "PlayHub",
    template: "%s | PlayHub",
  },
  description: "Descubrí, elegí y organizá tus próximos videojuegos en PlayHub.",
  icons: {
    icon: "/playhub-mark.png",
    apple: "/playhub-mark.png",
  },
};

export const viewport = {
  colorScheme: "dark",
  themeColor: "#050817",
};

export default function RootLayout({ children }) {
  return (
    <html lang="es">
      <body>
        <Providers>{children}</Providers>
      </body>
    </html>
  );
}
