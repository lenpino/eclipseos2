# Eclipse SWT para OS/2 (PM) — flujo de trabajo VM ↔ Git

Este repositorio es la conversión a Git del SVN original de netlabs
(`https://svn.netlabs.org/repos/swt`, r1–r270), importado con `git svn`:

- `main` ← `svn/trunk`
- `svn/watcom_ant17_java141` ← `branches/watcom_ant17_java141`

El desarrollo y la compilación se hacen dentro de la VM eComStation
(`X:\apps\dev\swt\trunk`); este repo en el Mac es donde se versiona y se
publica en GitHub.

## Scripts (`../tools`)

| Script | Uso |
|---|---|
| `vm.sh '<cmd>'` | Ejecuta un comando CMD.EXE en la VM. Encadenar con `&`, p.ej. `./vm.sh 'X: & cd \apps\dev\swt\trunk & ant.cmd compile'` |
| `vm-pull.sh` | Empaqueta `trunk` en la VM, lo trae y lo sincroniza sobre este repo (`rsync --delete`). Luego revisar `git status` y commitear. |
| `vm-push.sh [REV]` | Envía a la VM los ficheros cambiados respecto de `REV` (por defecto `HEAD`) más los no versionados. |

Configuración en `tools/vmenv.sh` (host, usuario, ruta; sobrescribible por
variables de entorno `VM_HOST`, `VM_USER`, `VM_PASS`, `VM_SWT_DIR`).

## Ciclo habitual

1. Editar en la VM (o en el Mac y `vm-push.sh`).
2. Compilar/probar en la VM: `ant.cmd compile`, `ant.cmd dll`.
3. `vm-pull.sh` → `git diff` → `git commit`.

## Notas

- Los fuentes están en CRLF; `.gitattributes` (`* -text`) evita conversiones.
- `ant.cmd dll` informa `BUILD SUCCESSFUL` aunque falle `nmake`: revisar la
  salida de VisualAge C++ (`CPPC…`, `NMAKE : fatal error`).
