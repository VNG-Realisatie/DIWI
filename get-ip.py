#!/usr/bin/env python3
import subprocess


def main():
    for service in [
        {"name": "frontend", "prefix": "http://", "postfix": ""},
        {"name": "backend", "prefix": "http://", "postfix": ":8080"},
        {"name": "keycloak", "prefix": "http://", "postfix": ":8080"},
        {"name": "database", "prefix": "", "postfix": ":5432"},
        {"name": "keycloak_database", "prefix": "", "postfix": ":5432"},
    ]:
        service_name = service['name']
        ip = (
            subprocess.check_output(
                [
                    "docker",
                    "inspect",
                    "-f",
                    "{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}",
                    f"vng-{service_name}-1",
                ]
            )
            .decode("utf8")
            .strip()
        )

        print(f"{service_name}: {service['prefix']}{ip}{service['postfix']}")


if __name__ == "__main__":
    main()
