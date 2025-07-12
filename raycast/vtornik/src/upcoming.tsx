import { useEffect, useState } from "react"
import { ActionPanel, Action, Grid } from "@raycast/api"

interface Movie {
  id: number
  title: string
  posterUrl: string
}

export default function Command() {
  const [movies, setMovies] = useState<Movie[]>([])
  const [isLoading, setIsLoading] = useState(true)
  useEffect(() => {
    async function fetchNowPlaying() {
      setIsLoading(true)
      const rs = await fetch("http://localhost:8080/json/movies/upcoming")
      const json = await rs.json()
      setMovies(json as Movie[])
      setIsLoading(false)
    }

    fetchNowPlaying()
  }, [])

  return (
    <Grid columns={4} inset={Grid.Inset.Zero} isLoading={isLoading}>
      {!isLoading &&
        movies.map((movie) => {
          return (
            <Grid.Item
              key={movie.id}
              content={{
                value: movie.posterUrl,
                tooltip: movie.title,
              }}
              title={movie.title}
              subtitle={movie.title}
              actions={
                <ActionPanel>
                  <Action.CopyToClipboard content={movie.title} />
                </ActionPanel>
              }
            />
          )
        })}
    </Grid>
  )
}
