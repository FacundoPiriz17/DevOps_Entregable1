import Image from "next/image";

export default function LoadingState({ label = "Cargando PlayHub..." }) {
  return (
    <div className="flex min-h-[60vh] flex-col items-center justify-center gap-3.5 text-sm font-semibold text-copy-soft" role="status">
      <span className="animate-float-mark inline-flex">
        <Image className="size-20.5 object-contain" src="/playhub-mark.png" alt="" width={88} height={88} />
      </span>
      <span>{label}</span>
    </div>
  );
}
