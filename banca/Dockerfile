FROM mcr.microsoft.com/dotnet/core/sdk:3.0.103-alpine
WORKDIR /banca
COPY . .
WORKDIR /banca/BanCaLiteNet
RUN dotnet publish -c Release -o /build

WORKDIR /build

ENTRYPOINT ["dotnet", "BanCaLiteNet.dll"]