# Configuración del Secret en GitHub

## Pasos para configurar el secret EC2_SSH_KEY:

1. Ve a tu repositorio en GitHub: https://github.com/JuampaRomero/PracticoJavaEE

2. Haz clic en **Settings** (Configuración)

3. En el menú lateral, busca **Secrets and variables** y haz clic en **Actions**

4. Haz clic en el botón **New repository secret**

5. Configura el secret con estos datos:
   - **Name**: `EC2_SSH_KEY`
   - **Secret**: Copia TODO el contenido del archivo `C:\Users\Usuario\Downloads\KeyPairForSSH.pem` (desde `-----BEGIN RSA PRIVATE KEY-----` hasta `-----END RSA PRIVATE KEY-----` inclusive)

6. Haz clic en **Add secret**

## Importante:
- Asegúrate de copiar TODO el contenido del archivo .pem, incluyendo las líneas BEGIN y END
- No agregues espacios adicionales al principio o final
- El secret debe contener exactamente 27 líneas

## Verificación:
Una vez configurado el secret, cuando hagas push a la rama main o master, el workflow de GitHub Actions se ejecutará automáticamente y desplegará tu aplicación en EC2.
