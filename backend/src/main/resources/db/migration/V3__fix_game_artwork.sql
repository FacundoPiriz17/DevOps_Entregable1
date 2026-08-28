-- Corrige portadas panorámicas o de baja resolución y agrega banners dedicados.
-- Se mantiene V2 sin cambios para conservar su checksum en bases ya migradas.

INSERT INTO imagen (url, texto_alternativo)
SELECT asset.url, asset.texto_alternativo
FROM (VALUES
    ('https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1687950/library_600x900_2x.jpg', 'Portada de Persona 5'),
    ('https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1687950/library_hero.jpg', 'Banner de Persona 5'),
    ('https://pisces.bbystatic.com/image2/BestBuy_US/images/products/5721/5721500_sd.jpg', 'Portada de The Legend of Zelda: Breath of the Wild'),
    ('https://assets.nintendo.com/image/upload/ar_16:9,c_lpad,w_1240/b_white/f_auto/q_auto/ncom/software/switch/70010000000025/7137262b5a64d921e193653f8aa0b722925abc5680380ca0e18a5cfd91697f58', 'Banner de The Legend of Zelda: Breath of the Wild'),
    ('https://images.launchbox-app.com/5bd97f0b-c784-4cf0-87e1-2b10f36637fa.jpg', 'Portada de Super Mario Odyssey'),
    ('https://assets.nintendo.com/image/upload/ar_16:9,c_lpad,w_1240/b_white/f_auto/q_auto/store/software/switch/70010000001130/c42553b4fd0312c31e70ec7468c6c9bccd739f340152925b9600631f2d29f8b5', 'Banner de Super Mario Odyssey'),
    ('https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/524220/library_hero.jpg', 'Banner de NieR: Automata'),
    ('https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/504230/library_hero.jpg', 'Banner de Celeste'),
    ('https://cdn2.steamgriddb.com/grid/5445c5fc4461b6d81db6224a6c28be2b.png', 'Portada de Bloodborne'),
    ('https://wallpaperset.com/w/full/3/b/0/250799.jpg', 'Banner de Bloodborne'),
    ('https://www.rockstargames.com/VI/-/opengraph-image.jpg?opengraph-image.0t8ty~nlmxq2s.jpg', 'Banner de Grand Theft Auto VI')
) AS asset(url, texto_alternativo)
WHERE NOT EXISTS (
    SELECT 1
    FROM imagen existing
    WHERE existing.url = asset.url
);

UPDATE imagen i
SET texto_alternativo = asset.texto_alternativo
FROM (VALUES
    ('https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1687950/library_600x900_2x.jpg', 'Portada de Persona 5'),
    ('https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1687950/library_hero.jpg', 'Banner de Persona 5'),
    ('https://pisces.bbystatic.com/image2/BestBuy_US/images/products/5721/5721500_sd.jpg', 'Portada de The Legend of Zelda: Breath of the Wild'),
    ('https://assets.nintendo.com/image/upload/ar_16:9,c_lpad,w_1240/b_white/f_auto/q_auto/ncom/software/switch/70010000000025/7137262b5a64d921e193653f8aa0b722925abc5680380ca0e18a5cfd91697f58', 'Banner de The Legend of Zelda: Breath of the Wild'),
    ('https://images.launchbox-app.com/5bd97f0b-c784-4cf0-87e1-2b10f36637fa.jpg', 'Portada de Super Mario Odyssey'),
    ('https://assets.nintendo.com/image/upload/ar_16:9,c_lpad,w_1240/b_white/f_auto/q_auto/store/software/switch/70010000001130/c42553b4fd0312c31e70ec7468c6c9bccd739f340152925b9600631f2d29f8b5', 'Banner de Super Mario Odyssey'),
    ('https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/524220/library_hero.jpg', 'Banner de NieR: Automata'),
    ('https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/504230/library_hero.jpg', 'Banner de Celeste'),
    ('https://cdn2.steamgriddb.com/grid/5445c5fc4461b6d81db6224a6c28be2b.png', 'Portada de Bloodborne'),
    ('https://wallpaperset.com/w/full/3/b/0/250799.jpg', 'Banner de Bloodborne'),
    ('https://www.rockstargames.com/VI/-/opengraph-image.jpg?opengraph-image.0t8ty~nlmxq2s.jpg', 'Banner de Grand Theft Auto VI')
) AS asset(url, texto_alternativo)
WHERE i.url = asset.url;

DELETE FROM juego_imagen ji
USING juego j
WHERE ji.identificador_juego = j.identificador
  AND (j.nombre, ji.tipo) IN (
      ('Persona 5', 'portada'),
      ('Persona 5', 'banner'),
      ('The Legend of Zelda: Breath of the Wild', 'portada'),
      ('The Legend of Zelda: Breath of the Wild', 'banner'),
      ('Super Mario Odyssey', 'portada'),
      ('Super Mario Odyssey', 'banner'),
      ('NieR: Automata', 'banner'),
      ('Celeste', 'banner'),
      ('Bloodborne', 'portada'),
      ('Bloodborne', 'banner'),
      ('Grand Theft Auto VI', 'banner')
  );

INSERT INTO juego_imagen (identificador_juego, id_imagen, tipo)
SELECT j.identificador, i.id_imagen, asset.tipo::tipo_imagen
FROM (VALUES
    ('Persona 5', 'https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1687950/library_600x900_2x.jpg', 'portada'),
    ('Persona 5', 'https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1687950/library_hero.jpg', 'banner'),
    ('The Legend of Zelda: Breath of the Wild', 'https://pisces.bbystatic.com/image2/BestBuy_US/images/products/5721/5721500_sd.jpg', 'portada'),
    ('The Legend of Zelda: Breath of the Wild', 'https://assets.nintendo.com/image/upload/ar_16:9,c_lpad,w_1240/b_white/f_auto/q_auto/ncom/software/switch/70010000000025/7137262b5a64d921e193653f8aa0b722925abc5680380ca0e18a5cfd91697f58', 'banner'),
    ('Super Mario Odyssey', 'https://images.launchbox-app.com/5bd97f0b-c784-4cf0-87e1-2b10f36637fa.jpg', 'portada'),
    ('Super Mario Odyssey', 'https://assets.nintendo.com/image/upload/ar_16:9,c_lpad,w_1240/b_white/f_auto/q_auto/store/software/switch/70010000001130/c42553b4fd0312c31e70ec7468c6c9bccd739f340152925b9600631f2d29f8b5', 'banner'),
    ('NieR: Automata', 'https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/524220/library_hero.jpg', 'banner'),
    ('Celeste', 'https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/504230/library_hero.jpg', 'banner'),
    ('Bloodborne', 'https://cdn2.steamgriddb.com/grid/5445c5fc4461b6d81db6224a6c28be2b.png', 'portada'),
    ('Bloodborne', 'https://wallpaperset.com/w/full/3/b/0/250799.jpg', 'banner'),
    ('Grand Theft Auto VI', 'https://www.rockstargames.com/VI/-/opengraph-image.jpg?opengraph-image.0t8ty~nlmxq2s.jpg', 'banner')
) AS asset(game_name, url, tipo)
JOIN juego j ON j.nombre = asset.game_name
JOIN imagen i ON i.url = asset.url;
