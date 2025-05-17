import socket
import sys

def main(dest_ip, dest_port):
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect((dest_ip, dest_port))

    while (True):
        command = input()
        s.send((command + '\n').encode())
        output = s.recv(4096)
        print(output.decode())

if __name__ == "__main__":
    dest_ip = sys.argv[1]
    dest_port = int(sys.argv[2])
    main(dest_ip, dest_port)